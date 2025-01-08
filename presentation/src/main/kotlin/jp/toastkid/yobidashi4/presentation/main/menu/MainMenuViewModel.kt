package jp.toastkid.yobidashi4.presentation.main.menu

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import jp.toastkid.yobidashi4.domain.model.chat.Chat
import jp.toastkid.yobidashi4.domain.model.file.ArticleFilesFinder
import jp.toastkid.yobidashi4.domain.model.file.LatestFileFinder
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.BarcodeToolTab
import jp.toastkid.yobidashi4.domain.model.tab.CalendarTab
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
import jp.toastkid.yobidashi4.domain.model.tab.CompoundInterestCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.ConverterToolTab
import jp.toastkid.yobidashi4.domain.model.tab.EditorSettingTab
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.FileRenameToolTab
import jp.toastkid.yobidashi4.domain.model.tab.FileTab
import jp.toastkid.yobidashi4.domain.model.tab.LoanCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.domain.model.tab.NotificationListTab
import jp.toastkid.yobidashi4.domain.model.tab.NumberPlaceGameTab
import jp.toastkid.yobidashi4.domain.model.tab.RouletteToolTab
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.tab.WebHistoryTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.model.web.user_agent.UserAgent
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import jp.toastkid.yobidashi4.domain.repository.notification.NotificationEventRepository
import jp.toastkid.yobidashi4.domain.service.archive.ZipArchiver
import jp.toastkid.yobidashi4.domain.service.article.finder.AsynchronousArticleIndexerService
import jp.toastkid.yobidashi4.domain.service.media.MediaFileFinder
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_left_panel_close
import jp.toastkid.yobidashi4.library.resources.ic_left_panel_open
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainMenuViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val setting: Setting by inject()

    private val asynchronousArticleIndexerService: AsynchronousArticleIndexerService by inject()

    private val webBookmarkRepository: BookmarkRepository by inject()

    private val currentUserAgent = mutableStateOf(UserAgent.findByName(setting.userAgentName()))

    fun makeNewArticle() {
        viewModel.makeNewArticle()
    }

    fun switchArticleListLabel(): String {
        return if (viewModel.openArticleList()) "Close article list" else "Open article list"
    }

    fun switchArticleListIconPath() =
        if (viewModel.openArticleList()) Res.drawable.ic_left_panel_close else Res.drawable.ic_left_panel_open

    fun switchArticleListShortcut(): KeyShortcut {
        return KeyShortcut(if (viewModel.openArticleList()) Key.DirectionLeft else Key.DirectionRight, ctrl = true, alt = true)
    }

    fun switchArticleList() {
        viewModel.switchArticleList()
    }

    fun switchFindArticle() {
        switchAggregationBox(7)
    }

    fun updateFinderIndex() {
        asynchronousArticleIndexerService.invoke(Dispatchers.IO)
    }

    fun dumpLatest() {
        ZipArchiver().invoke(
            LatestFileFinder().invoke(setting.articleFolderPath(), LocalDateTime.now().minusWeeks(1))
        )
        viewModel.openFile(Path.of("."))
    }

    fun dumpAll() {
        ZipArchiver().invoke(ArticleFilesFinder().invoke(setting.articleFolderPath()))
        viewModel.openFile(Path.of("."))
    }

    fun openArticleFolder() {
        viewModel.openFile(setting.articleFolderPath())
    }

    fun openUserFolder() {
        viewModel.openFile(Path.of("user"))
    }

    fun openDownloadFolder() {
        viewModel.openFile(Path.of("user/download"))
    }

    fun useEditorMenu(): Boolean {
        return viewModel.currentTab() is EditorTab
    }

    fun switchPreviewLabel(): String {
        val editorTab = viewModel.currentTab() as? EditorTab ?: return "Show preview"
        return if (editorTab.showPreview()) "Hide preview" else "Show preview"
    }

    fun switchPreview() {
        (viewModel.currentTab() as? EditorTab)?.switchPreview()
    }

    fun saveCurrentEditorTab() {
        viewModel.saveCurrentEditorTab()
    }

    fun saveAllEditorTab() {
        viewModel.saveAllEditorTab()
    }

    fun switchFind() {
        viewModel.switchFind()
    }

    fun openEditorSetting() {
        viewModel.openTab(EditorSettingTab())
    }

    fun closeCurrentTab(ifEmpty: () -> Unit) {
        if (viewModel.tabs.isNotEmpty()) {
            viewModel.closeCurrent()
            return
        }
        ifEmpty()
    }

    fun useAdditionalTabMenu(): Boolean {
        return viewModel.tabs.isNotEmpty()
    }

    fun closeAllTabs() {
        viewModel.closeAllTabs()
    }

    fun closeOtherTabs() {
        viewModel.closeOtherTabs()
    }

    fun copyTabsTitle() {
        val currentTab = viewModel.currentTab() ?: return
        ClipboardPutterService().invoke(currentTab.title())
    }

    fun currentIsWebTab(): Boolean {
        return viewModel.currentTab() is WebTab
    }

    fun currentIsEditableTab(): Boolean {
        return viewModel.currentTab() is MarkdownPreviewTab
    }

    fun openEditorTabWithCurrentTabsPath() {
        val markdownPreviewTab = viewModel.currentTab() as? MarkdownPreviewTab ?: return
        viewModel.edit(markdownPreviewTab.slideshowSourcePath())
    }

    fun copyTabsUrl() {
        val tab = viewModel.currentTab() as? WebTab ?: return
        ClipboardPutterService().invoke(tab.url())
    }

    fun copyTabsUrlAsMarkdownLink() {
        val tab = viewModel.currentTab() as? WebTab ?: return
        ClipboardPutterService().invoke(tab.markdownLink())
    }

    fun addWebBookmark() {
        val webTab = viewModel.currentTab() as? WebTab ?: return
        webBookmarkRepository.add(Bookmark.fromWebTab(webTab))
    }

    fun findSlideshowPath(): Path? {
        return when (val currentTab = viewModel.currentTab()) {
            is EditorTab -> currentTab.path
            is MarkdownPreviewTab -> currentTab.slideshowSourcePath()
            else -> null
        }
    }

    fun slideshow(slideshowSourcePath: Path) {
        viewModel.slideshow(slideshowSourcePath)
    }

    fun canMoveTab(): Boolean {
        return viewModel.tabs.size > 1
    }

    fun moveTabIndex(i: Int) {
        viewModel.moveTabIndex(i)
    }

    fun tabCount(): Int {
        return viewModel.tabs.size
    }

    fun makeTabIndexShortcut(index: Int): KeyShortcut {
        return  KeyShortcut(when (index) {
            1 -> Key.One
            2 -> Key.Two
            3 -> Key.Three
            4 -> Key.Four
            5 -> Key.Five
            6 -> Key.Six
            7 -> Key.Seven
            8 -> Key.Eight
            9 -> Key.Nine
            10 -> Key.Zero
            else -> Key.One
        }, alt = true)
    }

    fun setSelectedIndex(i: Int) {
        viewModel.setSelectedIndex(i)
    }

    fun openChatTab() {
        viewModel.openTab(ChatTab(Chat(mutableStateListOf())))
    }

    fun openBookmarkTab() {
        viewModel.openTab(WebBookmarkTab())
    }

    fun openCalendarTab() {
        viewModel.openTab(CalendarTab())
    }

    fun searchWithSelectedText() {
        viewModel.webSearchWithSelectedText()
    }

    fun openAggregationBox() {
        switchAggregationBox(0)
    }

    private fun switchAggregationBox(initialChoosed: Int) {
        if (viewModel.showAggregationBox().not()) {
            viewModel.setInitialAggregationType(initialChoosed)
        }
        viewModel.switchAggregationBox(viewModel.showAggregationBox().not())
    }

    fun openWebHistoryTab() {
        viewModel.openTab(WebHistoryTab())
    }

    fun switchWebSearchBox() {
        viewModel.setShowWebSearch(viewModel.showWebSearch().not())
    }

    fun openBarcodeToolTab() {
        viewModel.openTab(BarcodeToolTab())
    }

    fun openConverterToolTab() {
        viewModel.openTab(ConverterToolTab())
    }

    fun openFileRenameToolTab() {
        viewModel.openTab(FileRenameToolTab())
    }

    fun openRouletteToolTab() {
        viewModel.openTab(RouletteToolTab())
    }

    fun openCompoundInterestCalculatorTab() {
        viewModel.openTab(CompoundInterestCalculatorTab())
    }

    fun openLoanCalculatorTab() {
        viewModel.openTab(LoanCalculatorTab())
    }

    fun openNumberPlaceTab() {
        viewModel.openTab(NumberPlaceGameTab())
    }

    fun openMusicPlayerTab() {
        val mediaFileFolderPath = setting.mediaFolderPath() ?: return
        viewModel.openFileListTab(
            "Music",
            MediaFileFinder().invoke(mediaFileFolderPath),
            FileTab.Type.MUSIC
        )
    }

    fun toggleWorldTimeShortcut() =
        KeyShortcut(if (viewModel.openWorldTime()) Key.DirectionRight else Key.DirectionLeft, ctrl = true, shift = true)

    fun toggleWorldTime() {
        viewModel.toggleWorldTime()
    }

    fun isSelectedUserAgent(it: UserAgent): Boolean {
        return it == currentUserAgent.value
    }

    fun chooseUserAgent(it: UserAgent) {
        setting.setUserAgentName(it.name)
        setting.save()

        currentUserAgent.value = it

        viewModel.showSnackbar("Please would you restart this app?")
    }

    fun openNotificationList() {
        viewModel.openTab(NotificationListTab())
    }

    fun openNotificationFile() {
        val path = Path.of("user/notification/list.tsv")
        viewModel.openFile(path)
    }

    fun exportNotifications() {
        viewModel.showSnackbar("Done export.", "Open") {
            val path = Path.of(
                "notification${
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("_yyyyMMdd_HHmmss"))
                }.tsv"
            )
            Files.write(path, object : KoinComponent { val repo: NotificationEventRepository by inject() }.repo.readAll().map { it.toTsv() } )
            viewModel.openFile(Path.of("."))
        }
    }

    fun toggleFullscreenLabel(): String {
        return viewModel.toggleFullscreenLabel()
    }

    fun toggleFullscreen() {
        viewModel.toggleFullscreen()
    }

    fun toggleNarrowWindow() {
        viewModel.toggleNarrowWindow()
    }

    fun toDefaultWindowSize() {
        viewModel.toDefaultWindowSize()
    }

    fun loadBackgroundImage() {
        viewModel.loadBackgroundImage()
    }

    fun switchUseBackground() {
        viewModel.switchUseBackground()
    }

    fun switchDarkMode() {
        viewModel.switchDarkMode()
    }

    fun openArticleTemplate() {
        val path = Path.of("user/article_template.txt")
        viewModel.openFile(path)
    }

    fun switchMemoryUsageBoxLabel() =
        if (viewModel.openMemoryUsageBox()) "Close memory usage" else "Memory usage"

    fun switchMemoryUsageBox() {
        viewModel.switchMemoryUsageBox()
    }

    fun openLogViewerTab() {
        val logFilePath = Path.of("temporary/logs/app.log")
        viewModel.openTextFile(logFilePath)
    }

    fun openProperty() {
        val logFilePath = Path.of("user/setting.properties")
        viewModel.openFile(logFilePath)
    }

}