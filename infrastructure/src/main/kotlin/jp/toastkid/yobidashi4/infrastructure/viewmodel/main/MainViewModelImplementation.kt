package jp.toastkid.yobidashi4.infrastructure.viewmodel.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.TextContextMenu
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import java.awt.Desktop
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import javax.imageio.ImageIO
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.FileTab
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.TextFileViewerTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.search.SearchUrlFactory
import jp.toastkid.yobidashi4.domain.repository.web.history.WebHistoryRepository
import jp.toastkid.yobidashi4.domain.service.archive.TopArticleLoaderService
import jp.toastkid.yobidashi4.domain.service.editor.EditorTabFileStore
import jp.toastkid.yobidashi4.infrastructure.service.media.MediaPlayerInvokerImplementation
import jp.toastkid.yobidashi4.presentation.editor.finder.FindOrder
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.extension
import kotlin.io.path.inputStream
import kotlin.math.max
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class MainViewModelImplementation : MainViewModel, KoinComponent {

    private val setting: Setting by inject()

    private val topArticleLoaderService: TopArticleLoaderService by inject()

    private val webViewPool: WebViewPool by inject()

    private val _darkMode = mutableStateOf(setting.darkMode())

    override fun darkMode(): Boolean {
        return _darkMode.value
    }

    override fun switchDarkMode() {
        _darkMode.value = _darkMode.value.not()
        setting.setDarkMode(_darkMode.value)
    }

    private val backgroundImage = mutableStateOf(ImageBitmap(0, 0))

    override fun backgroundImage(): ImageBitmap = backgroundImage.value

    override fun showBackgroundImage(): Boolean {
        return backgroundImage().height != 0
    }

    override fun switchUseBackground() {
        setting.switchUseBackground()

        if (setting.useBackground().not()) {
            backgroundImage.value = ImageBitmap(0, 0)
        } else {
            loadBackgroundImage()
        }
    }

    override fun loadBackgroundImage() {
        if (setting.useBackground().not()) {
            return
        }

        val imageFolder = Path.of("user/background")
        if (Files.exists(imageFolder).not()) {
            return
        }

        val images = Files.list(imageFolder).collect(Collectors.toList())
        if (images.isNotEmpty()) {
            backgroundImage.value = images[((images.size - 1) * Math.random()).roundToInt()].inputStream().use {
                ImageIO.read(it).toComposeImageBitmap()
            }
        }
    }

    private val _selected =  mutableStateOf(0)

    override val selected: State<Int> = _selected

    override fun setSelectedIndex(newIndex: Int) {
        if (newIndex < 0 || newIndex >= _tabs.size) {
            return
        }
        _selected.value = newIndex
    }

    private val _tabs = mutableStateListOf<Tab>()

    override val tabs: SnapshotStateList<Tab> = _tabs

    override fun currentTab(): Tab? {
        if (tabs.isEmpty() || selected.value < 0 || tabs.size <= selected.value) {
            return null
        }
        return tabs.get(selected.value)
    }

    override fun openFileListTab(title: String, items: Collection<Path>, closeable: Boolean, type: FileTab.Type) {
        openTab(FileTab(title, items.sortedByDescending { Files.getLastModifiedTime(it).toMillis() }, closeable, type))
    }

    override fun openFile(path: Path, onBackground: Boolean) {
        if (path.extension == "m4a" || path.extension == "mp3") {
            MediaPlayerInvokerImplementation().invoke(path)
            return
        }

        if (Files.exists(path).not()) {
            return
        }

        Desktop.getDesktop().open(path.toFile())
    }

    override fun openPreview(path: Path, onBackground: Boolean) {
        val tab = MarkdownPreviewTab.with(path)
        if (onBackground) {
            _tabs.add(tab)
            return
        }
        openTab(tab)
    }

    override fun removeTabAt(index: Int) {
        val targetTab = _tabs.get(index)
        if (targetTab.closeable().not()) {
            showSnackbar("Cannot close this tab.")
            return
        }
        val presentSize = _tabs.size
        _tabs.removeAt(index)
        if (presentSize == _tabs.size || _selected.value < index) {
            return
        }
        _selected.value = max(0, _selected.value - 1)

        if (targetTab is WebTab) {
            webViewPool.dispose(targetTab.id())
        }
    }

    override fun openTextFile(path: Path) {
        openTab(TextFileViewerTab(path))
    }

    override fun openUrl(url: String, background: Boolean) {
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            showSnackbar("Invalid URL. $url")
            return
        }

        val newTab = WebTab(title = url, url = url)
        if (background) {
            _tabs.add(newTab)
            return
        }

        openTab(newTab)
    }

    override fun webSearch(query: String?, background: Boolean) {
        if (query.isNullOrBlank()) {
            return
        }

        openUrl(SearchUrlFactory().invoke(query), background)
    }

    override fun edit(path: Path, onBackground: Boolean) {
        if (Files.exists(path).not()) {
            return
        }

        val indexOfFirst = tabs.indexOfFirst { it is EditorTab && it.path == path }
        if (indexOfFirst != -1) {
            _selected.value = indexOfFirst
            return
        }

        val tab = EditorTab(path)
        (tab as? EditorTab)?.loadContent()
        if (onBackground) {
            _tabs.add(tab)
            return
        }
        openTab(tab)
    }

    override fun browseUri(uri: String?) {
        if (uri.isNullOrBlank()) {
            return
        }

        val urlString = if (uri.startsWith("http://") || uri.startsWith("https://")) {
            uri
        } else {
            (currentTab() as? WebTab)?.url()
        } ?: return

        Desktop.getDesktop().browse(URI(urlString))
    }

    private val webHistoryRepository: WebHistoryRepository by inject()

    override fun updateWebTab(id: String, title: String, url: String?) {
        val updated = _tabs.firstOrNull { it is WebTab && it.id() == id } as? WebTab
        updated?.updateTitleAndUrl(title, url)

        url?.let {
            CoroutineScope(Dispatchers.IO).launch {
                webHistoryRepository.add(title, url)
            }
        }
    }

    override fun closeCurrent() {
        removeTabAt(selected.value)
    }

    override fun closeOtherTabs() {
        val current = currentTab()
        val iterator = _tabs.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next == current) {
                continue
            }
            if (next is WebTab) {
                webViewPool.dispose(next.id())
            }
            iterator.remove()
        }
        _selected.value = 0
    }

    override fun closeAllTabs() {
        val targetIds = _tabs.filterIsInstance<WebTab>().map { it.id() }
        _tabs.clear()
        _selected.value = -1
        targetIds.forEach(webViewPool::dispose)
    }

    override fun addNewArticle(path: Path) {
        _articles.add(0, path)
    }

    private val editorTabFileStore = EditorTabFileStore()

    override fun saveCurrentEditorTab() {
        val tab = currentTab() as? EditorTab ?: return

        editorTabFileStore(tab)
    }

    override fun saveAllEditorTab() {
        tabs.filterIsInstance(EditorTab::class.java).forEach { editorTabFileStore(it) }
    }

    override fun updateEditorContent(path: Path, text: String, caretPosition: Int, scroll: Double, resetEditing: Boolean) {
        val editorTab = tabs.filterIsInstance(EditorTab::class.java).firstOrNull { it.path == path } ?: return
        editorTab.setContent(text, resetEditing)
        if (caretPosition != -1) {
            editorTab.setCaretPosition(caretPosition)
        }

        if (scroll >= 0) {
            editorTab.setScroll(scroll)
        }
    }

    override fun openingEditor(): Boolean {
        return currentTab() is EditorTab
    }

    private val _showWebSearch = mutableStateOf(false)

    override fun showWebSearch(): Boolean = _showWebSearch.value

    override fun setShowWebSearch(newState: Boolean) {
        _showWebSearch.value = newState
    }

    private val _showAggregationBox = mutableStateOf(false)

    override fun showAggregationBox(): Boolean {
        return _showAggregationBox.value
    }

    override fun switchAggregationBox(newState: Boolean) {
        _showAggregationBox.value = newState
    }

    private var initialAggregationType = 0

    override fun initialAggregationType(): Int = initialAggregationType

    override fun setInitialAggregationType(ordinal: Int) {
        initialAggregationType = ordinal
    }

    private val showInputBox = mutableStateOf(false)
    private var inputBoxAction: ((String) -> Unit)? = null

    override fun showInputBox(): Boolean {
        return showInputBox.value
    }

    override fun setShowInputBox(action: ((String) -> Unit)?) {
        showInputBox.value = action != null
        inputBoxAction = action
    }

    override fun invokeInputAction(input: String?) {
        if (input.isNullOrBlank()) {
            return
        }
        inputBoxAction?.let { it(input) }
    }

    private val window = WindowState(
        size = DpSize(width = 1100.dp, height = 700.dp),
        position = WindowPosition(Alignment.Center)
    )

    override fun windowState() = window

    override fun toggleFullscreen() {
        window.placement = if (window.placement == WindowPlacement.Maximized) {
            WindowPlacement.Floating
        } else {
            WindowPlacement.Maximized
        }
    }

    override fun toggleFullscreenLabel(): String {
        return if (window.placement == WindowPlacement.Maximized) "Exit full screen" else "Full screen"
    }

    override fun toggleNarrowWindow() {
        windowState().size = DpSize(if (windowState().size.width <= 600.dp) 1100.dp else 520.dp, windowState().size.height)
    }

    override fun openTab(tab: Tab) {
        val newIndex = if (_tabs.isEmpty()) 0 else _selected.value + 1
        _tabs.add(newIndex, tab)
        _selected.value = newIndex
    }

    private val _snackbarHostState = SnackbarHostState()

    override fun snackbarHostState(): SnackbarHostState {
        return _snackbarHostState
    }

    override fun showingSnackbar(): Boolean = snackbarHostState().currentSnackbarData != null

    override fun showSnackbar(message: String, actionLabel: String?, action: () -> Unit) {
        if (actionLabel == null) {
            CoroutineScope(Dispatchers.Default).launch {
                _snackbarHostState.currentSnackbarData?.dismiss()
                _snackbarHostState.showSnackbar(message)
            }
            return
        }

        CoroutineScope(Dispatchers.Default).launch {
            _snackbarHostState.currentSnackbarData?.dismiss()
            val snackbarResult = _snackbarHostState.showSnackbar(
                message,
                actionLabel,
                SnackbarDuration.Long
            )
            when (snackbarResult) {
                SnackbarResult.Dismissed -> Unit
                SnackbarResult.ActionPerformed -> {
                    action()
                }
            }
        }
    }

    private val _openArticleList = mutableStateOf(false)

    override fun openArticleList(): Boolean = _openArticleList.value

    override fun switchArticleList() {
        _openArticleList.value = _openArticleList.value.not()
    }

    override fun hideArticleList() {
        _openArticleList.value = false
    }

    private val _articles = mutableStateListOf<Path>()

    override fun articles(): List<Path> {
        return _articles
    }

    override fun reloadAllArticle() {
        _articles.addAll(topArticleLoaderService.invoke())
        _openArticleList.value = true
    }

    private val openMemoryUsage = mutableStateOf(false)

    override fun openMemoryUsageBox(): Boolean {
        return openMemoryUsage.value
    }

    override fun switchMemoryUsageBox() {
        openMemoryUsage.value = openMemoryUsage.value.not()
    }

    private val openFind = mutableStateOf(false)

    private val findInput = mutableStateOf(TextFieldValue())
    private val replaceInput = mutableStateOf(TextFieldValue())

    override fun openFind() = openFind.value

    override fun switchFind() {
        openFind.value = openFind.value.not()

        if (openFind.value.not()) {
            findInput.value = TextFieldValue()
            CoroutineScope(Dispatchers.Unconfined).launch {
                _finderFlow.emit(FindOrder.EMPTY)
            }
        }
    }

    override fun inputValue() = findInput.value

    override fun replaceInputValue() = replaceInput.value

    private val _finderFlow = MutableSharedFlow<FindOrder>(1)

    override fun finderFlow(): Flow<FindOrder> {
        return _finderFlow.asSharedFlow()
    }

    override fun onFindInputChange(value: TextFieldValue) {
        findInput.value = TextFieldValue(value.text, value.selection, value.composition)
        findDown()
    }

    override fun onReplaceInputChange(value: TextFieldValue) {
        replaceInput.value = TextFieldValue(value.text, value.selection, value.composition)
    }

    override fun findUp() {
        CoroutineScope(Dispatchers.Default).launch {
            _finderFlow.emit(FindOrder(findInput.value.text, replaceInput.value.text, upper = true))
        }
    }

    override fun replaceAll() {
        CoroutineScope(Dispatchers.Default).launch {
            _finderFlow.emit(FindOrder(findInput.value.text, replaceInput.value.text, invokeReplace = true))
        }
    }

    override fun findDown() {
        CoroutineScope(Dispatchers.Default).launch {
            _finderFlow.emit(FindOrder(findInput.value.text, replaceInput.value.text, upper = false))
        }
    }

    private val findStatus = mutableStateOf("")

    override fun setFindStatus(status: String) {
        findStatus.value = status
    }

    override fun findStatus(): String {
        return findStatus.value
    }

    private val droppedPathFlow = MutableSharedFlow<Path>()
    override fun droppedPathFlow(): Flow<Path> {
        return droppedPathFlow.asSharedFlow()
    }

    override fun emitDroppedPath(paths: Collection<Path>) {
        CoroutineScope(Dispatchers.IO).launch {
            paths.forEach { droppedPathFlow.emit(it) }
        }
    }

    private val slideshowState = mutableStateOf<Path?>(null)

    override fun slideshowPath() = slideshowState.value

    override fun slideshow(path: Path) {
        slideshowState.value = path
    }

    override fun closeSlideshow() {
        slideshowState.value = null
    }

    @OptIn(ExperimentalFoundationApi::class)
    private var textManager: TextContextMenu.TextManager? = null

    @OptIn(ExperimentalFoundationApi::class)
    override fun setTextManager(textManager: TextContextMenu.TextManager) {
        this.textManager = textManager
    }

    @OptIn(ExperimentalFoundationApi::class)
    override fun selectedText(): String? {
        return textManager?.selectedText?.text
    }

}