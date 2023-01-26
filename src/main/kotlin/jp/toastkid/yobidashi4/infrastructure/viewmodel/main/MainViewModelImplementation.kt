package jp.toastkid.yobidashi4.infrastructure.viewmodel.main

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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import javax.imageio.ImageIO
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.browser.BrowserPool
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.FileTab
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.domain.model.tab.TextFileViewerTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.service.archive.TopArticleLoaderService
import jp.toastkid.yobidashi4.domain.service.media.MediaPlayerInvoker
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.extension
import kotlin.io.path.inputStream
import kotlin.math.max
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class MainViewModelImplementation : MainViewModel, KoinComponent {

    private val setting: Setting by inject()

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

    override fun loadBackgroundImage() {
        val imageFolder = Paths.get("user/background")
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

    override fun openFileListTab(title: String, items: Collection<Path>, closeable: Boolean, type: FileTab.Type) {
        openTab(FileTab(title, items.sortedByDescending { Files.getLastModifiedTime(it).toMillis() }, closeable, type))
    }

    override fun openAggregationResultTab(title: String, result: AggregationResult) {
        _tabs.add(TableTab(title, result, true))
        _selected.value = _tabs.size - 1
    }

    override fun openFile(path: Path, onBackground: Boolean) {
        if (path.extension == "m4a" || path.extension == "mp3") {
            MediaPlayerInvoker().invoke(path)
            return
        }

        if (tabs.filterIsInstance(EditorTab::class.java).any { it.path == path }
            || Files.exists(path).not()) {
            return
        }

        val tab = EditorTab(path)
        if (onBackground) {
            _tabs.add(tab)
            return
        }
        openTab(tab)
    }

    override fun removeTabAt(index: Int) {
        val targetTab = _tabs.get(index)
        if (targetTab.closeable().not()) {
            return
        }
        val presentSize = _tabs.size
        _tabs.removeAt(index)
        if (presentSize == _tabs.size || _selected.value < index) {
            return
        }
        _selected.value = max(0, _selected.value - 1)

        if (targetTab is WebTab) {
            object : KoinComponent { val browserPool: BrowserPool by inject() }.browserPool.dispose(targetTab.id())
        }
    }

    override fun openTextFile(path: Path) {
        openTab(TextFileViewerTab(path))
    }

    override fun openUrl(url: String, background: Boolean) {
        val newTab = WebTab(title = mutableStateOf(url), url = url)
        if (background) {
            _tabs.add(newTab)
            return
        }

        openTab(newTab)
    }

    override fun updateWebTab(id: String, title: String) {
        val webTab = _tabs.filterIsInstance<WebTab>().firstOrNull { it.id() == id } ?: return
        webTab.updateTitle(title)
    }

    override fun closeCurrent() {
        removeTabAt(selected.value)
    }

    override fun addNewArticle(path: Path) {
        _articles.add(0, path)
    }

    override fun updateEditorContent(path: Path, text: String, caretPosition: Int, resetEditing: Boolean) {
        val editorTab = tabs.filterIsInstance(EditorTab::class.java).firstOrNull { it.path == path }
        editorTab?.setContent(text, resetEditing)
        if (caretPosition != -1) {
            editorTab?.setCaretPosition(caretPosition)
        }
    }

    override fun openingEditor(): Boolean {
        return _tabs.get(selected.value) is EditorTab
    }

    private val _showWebSearch = mutableStateOf(false)

    override val showWebSearch: State<Boolean> = _showWebSearch

    override fun setShowWebSearch(newState: Boolean) {
        _showWebSearch.value = newState
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

    override fun openTab(tab: Tab) {
        val newIndex = if (_tabs.isEmpty()) 0 else _selected.value + 1
        _tabs.add(newIndex, tab)
        _selected.value = newIndex
    }

    private val _snackbarHostState = SnackbarHostState()

    override fun snackbarHostState(): SnackbarHostState {
        return _snackbarHostState
    }

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
                actionLabel ?: "",
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

    private val _articles = mutableListOf<Path>()

    override fun articles(): List<Path> {
        return _articles
    }

    override fun reloadAllArticle() {
        _articles.addAll(TopArticleLoaderService().invoke())
        _openArticleList.value = true
    }

}