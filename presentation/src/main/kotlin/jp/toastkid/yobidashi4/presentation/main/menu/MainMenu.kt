package jp.toastkid.yobidashi4.presentation.main.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import java.awt.Desktop
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.domain.model.file.ArticleFilesFinder
import jp.toastkid.yobidashi4.domain.model.file.LatestFileFinder
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.BarcodeToolTab
import jp.toastkid.yobidashi4.domain.model.tab.CalendarTab
import jp.toastkid.yobidashi4.domain.model.tab.CompoundInterestCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.ConverterToolTab
import jp.toastkid.yobidashi4.domain.model.tab.EditorSettingTab
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.FileRenameToolTab
import jp.toastkid.yobidashi4.domain.model.tab.FileTab
import jp.toastkid.yobidashi4.domain.model.tab.LoanCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.domain.model.tab.NumberPlaceGameTab
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.tab.WebHistoryTab
import jp.toastkid.yobidashi4.domain.model.web.user_agent.UserAgent
import jp.toastkid.yobidashi4.domain.service.archive.ZipArchiver
import jp.toastkid.yobidashi4.domain.service.media.MediaFileFinder
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.nameWithoutExtension
import kotlin.math.min
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FrameWindowScope.MainMenu(exitApplication: () -> Unit) {
    val koin = object: KoinComponent {
        val viewModel: MainViewModel by inject()
        val setting: Setting by inject()
        val articleFactory: ArticleFactory by inject()
    }
    val viewModel = koin.viewModel
    val setting = koin.setting

    MenuBar {
        Menu("File") {
            Item("Make new", shortcut = KeyShortcut(Key.N, ctrl = true), icon = painterResource("images/icon/ic_new_article.xml")) {
                viewModel.setShowInputBox { input ->
                    if (existsArticle(input, setting.articleFolderPath())) {
                        return@setShowInputBox
                    }

                    val article = koin.articleFactory.withTitle(input)
                    article.makeFile { "# ${article.getTitle()}" }
                    viewModel.addNewArticle(article.path())
                    viewModel.edit(article.path())
                }
            }

            Item(
                if (viewModel.openArticleList()) "Close article list" else "Open article list",
                icon = painterResource("images/icon/ic_left_panel_${if (viewModel.openArticleList()) "close" else "open"}.xml"),
                shortcut = KeyShortcut(if (viewModel.openArticleList()) Key.DirectionLeft else Key.DirectionRight, alt = true)
            ) {
                viewModel.switchArticleList()
            }

            Item("Find", icon = painterResource("images/icon/ic_search.xml"), shortcut = KeyShortcut(Key.F, alt = true)) {
                if (viewModel.showAggregationBox().not()) {
                    viewModel.setInitialAggregationType(7)
                }
                viewModel.switchAggregationBox(viewModel.showAggregationBox().not())
            }
            Item("Dump latest", icon = painterResource("images/icon/ic_dump.xml")) {
                ZipArchiver().invoke(
                    LatestFileFinder().invoke(setting.articleFolderPath(), LocalDateTime.now().minusWeeks(1))
                )
                Desktop.getDesktop().open(File("."))
            }
            Item("Dump all") {
                ZipArchiver().invoke(ArticleFilesFinder().invoke(setting.articleFolderPath()))
                Desktop.getDesktop().open(File("."))
            }
            Item("Open article folder", icon = painterResource("images/icon/ic_article_folder.xml"), shortcut = KeyShortcut(Key.O, alt = true)) {
                Desktop.getDesktop().open(setting.articleFolderPath().toFile())
            }
            Item("Open user folder", icon = painterResource("images/icon/ic_user_folder.xml"), shortcut = KeyShortcut(Key.U, alt = true)) {
                Desktop.getDesktop().open(Path.of("user").toFile())
            }
            Item("Exit", icon = painterResource("images/icon/ic_exit.xml")) {
                exitApplication()
            }
        }
        val currentTab = viewModel.currentTab()
        if (currentTab is EditorTab) {
            Menu("Edit") {
                Item(if (currentTab.showPreview()) "Hide preview" else "Show preview", shortcut = KeyShortcut(Key.M, ctrl = true), icon = painterResource("images/icon/ic_markdown.xml")) {
                    currentTab.switchPreview()
                }
                Item("Save", shortcut = KeyShortcut(Key.S, ctrl = true), icon = painterResource("images/icon/ic_save.xml")) {
                    viewModel.saveCurrentEditorTab()
                }
                Item("Save all", shortcut = KeyShortcut(Key.S, ctrl = true, shift = true), icon = painterResource("images/icon/ic_save.xml")) {
                    viewModel.saveCurrentEditorTab()
                }
                Item("Replace", shortcut = KeyShortcut(Key.R, ctrl = true), icon = painterResource("images/icon/ic_replace.xml")) {
                    viewModel.switchFind()
                }
                Item("Editor's Color & Font") {
                    viewModel.openTab(EditorSettingTab())
                }
            }
        }

        Menu("Tab") {
            Item("Close tab", shortcut = KeyShortcut(Key.W, ctrl = true), icon = painterResource("images/icon/ic_tab_close.xml")) {
                if (viewModel.tabs.size != 0) {
                    viewModel.closeCurrent()
                    return@Item
                }
                exitApplication()
            }

            if (viewModel.tabs.isNotEmpty()) {
                Item("Close all tabs", shortcut = KeyShortcut(Key.W, alt = true), icon = painterResource("images/icon/ic_clean.xml")) {
                    viewModel.closeAllTabs()
                }

                Item("Close other tabs", icon = painterResource("images/icon/ic_close_other_tabs.xml")) {
                    viewModel.closeOtherTabs()
                }
                Item("Copy tab's title", icon = painterResource("images/icon/ic_clipboard.xml")) {
                    ClipboardPutterService().invoke(viewModel.currentTab()?.title())
                }
            }

            when (currentTab) {
                is EditorTab -> currentTab.path
                is MarkdownPreviewTab -> currentTab.slideshowSourcePath()
                else -> null
            }?.let { slideshowSourcePath ->
                Item("Slideshow", shortcut = KeyShortcut(Key.F5), icon = painterResource("images/icon/ic_slideshow.xml")) {
                    viewModel.slideshow(slideshowSourcePath)
                }
            }

            Item(
                "Find in page",
                icon = painterResource("images/icon/ic_search.xml"),
                shortcut = KeyShortcut(Key.F, ctrl = true)
            ) {
                viewModel.switchFind()
            }

            if (viewModel.tabs.size > 1) {
                Item("Move previous tab", icon = painterResource("images/icon/ic_back.xml"), shortcut = KeyShortcut(Key.PageUp, ctrl = true)) {
                    if (viewModel.tabs.isEmpty()) {
                        return@Item
                    }

                    val nextIndex = if (viewModel.selected.value == 0) viewModel.tabs.size - 1 else viewModel.selected.value - 1
                    viewModel.setSelectedIndex(nextIndex)
                }

                Item("Move next tab", icon = painterResource("images/icon/ic_forward.xml"), shortcut = KeyShortcut(Key.PageDown, ctrl = true)) {
                    if (viewModel.tabs.isEmpty()) {
                        return@Item
                    }

                    val nextIndex = if (viewModel.selected.value == viewModel.tabs.size - 1) 0 else viewModel.selected.value + 1
                    viewModel.setSelectedIndex(nextIndex)
                }
            }

            (1 .. min(10, viewModel.tabs.size)).forEach {
                Item("Tab $it", shortcut = KeyShortcut(when (it) {
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
                }, alt = true)) {
                    viewModel.setSelectedIndex(it - 1)
                }
            }
        }

        Menu("Tool") {
            Item("Bookmark", shortcut = KeyShortcut(Key.B, alt = true), icon = painterResource("images/icon/ic_bookmark.xml")) {
                viewModel.openTab(WebBookmarkTab())
            }
            Item("Calendar", shortcut = KeyShortcut(Key.C, alt = true), icon = painterResource("images/icon/ic_calendar.xml")) {
                viewModel.openTab(CalendarTab())
            }
            Item("Aggregation", shortcut = KeyShortcut(Key.A, alt = true), icon = painterResource("images/icon/ic_aggregation.xml")) {
                if (viewModel.showAggregationBox().not()) {
                    viewModel.setInitialAggregationType(0)
                }
                viewModel.switchAggregationBox(viewModel.showAggregationBox().not())
            }
            Item("Web history", shortcut = KeyShortcut(Key.H, alt = true), icon = painterResource("images/icon/ic_history.xml")) {
                viewModel.openTab(WebHistoryTab())
            }
            Item("Web search", shortcut = KeyShortcut(Key.S, alt = true), icon = painterResource("images/icon/ic_search.xml")) {
                viewModel.setShowWebSearch(viewModel.showWebSearch().not())
            }
            Item("Barcode tool", icon = painterResource("images/icon/ic_barcode.xml")) {
                viewModel.openTab(BarcodeToolTab())
            }
            Item("Converter", icon = painterResource("images/icon/ic_converter.xml")) {
                viewModel.openTab(ConverterToolTab())
            }
            Item("File rename", icon = painterResource("images/icon/ic_rename.xml")) {
                viewModel.openTab(FileRenameToolTab())
            }
            Item("Compound interest calculator", icon = painterResource("images/icon/ic_elevation.xml")) {
                viewModel.openTab(CompoundInterestCalculatorTab())
            }
            Item("Loan calculator", shortcut = KeyShortcut(Key.L, alt = true), icon = painterResource("images/icon/ic_home.xml")) {
                viewModel.openTab(LoanCalculatorTab())
            }
            Item("Number place", shortcut = KeyShortcut(Key.N, alt = true), icon = painterResource("images/icon/ic_number_place.xml")) {
                viewModel.openTab(NumberPlaceGameTab())
            }
            Item("Music player", shortcut = KeyShortcut(Key.M, alt = true), icon = painterResource("images/icon/ic_music.xml")) {
                val mediaFileFolderPath = setting.mediaFolderPath() ?: return@Item
                viewModel.openFileListTab(
                    "Music",
                    MediaFileFinder().invoke(mediaFileFolderPath),
                    true,
                    FileTab.Type.MUSIC
                )
            }
        }

        Menu("User agent") {
            val current = remember { mutableStateOf(UserAgent.findByName(setting.userAgentName())) }
            UserAgent.values().forEach {
                RadioButtonItem(it.title(), selected = (it == current.value)) {
                    setting.setUserAgentName(it.name)
                    setting.save()
                    current.value = it
                    viewModel.showSnackbar("Please would you restart this app?")
                }
            }
        }

        Menu("Window") {
            Item(
                viewModel.toggleFullscreenLabel(),
                shortcut = KeyShortcut(key = Key.F11),
                icon = painterResource("images/icon/ic_fullscreen.xml")
            ) {
                viewModel.toggleFullscreen()
            }
            Item("Narrow window", icon = painterResource("images/icon/ic_narrow_window.xml")) {
                viewModel.toggleNarrowWindow()
            }
            Item(
                "Re-lottery background",
                shortcut = KeyShortcut(key = Key.B, alt = true, ctrl = true),
                icon = painterResource("images/icon/ic_wallpaper.xml")
            ) {
                viewModel.loadBackgroundImage()
            }
        }

        Menu("Setting") {
            Item("Switch dark mode", icon = painterResource("images/icon/ic_dark_mode.xml"), shortcut = KeyShortcut(key = Key.D, alt = true)) {
                viewModel.switchDarkMode()
            }
            Item("Open article template", icon = painterResource("images/icon/ic_user_template.xml")) {
                val path = Path.of("user/article_template.txt")
                Desktop.getDesktop().open(path.toFile())
            }
            Item(
                if (viewModel.openMemoryUsageBox()) "Close memory usage" else "Memory usage",
                icon = painterResource("images/icon/ic_memory.xml")
            ) {
                viewModel.switchMemoryUsageBox()
            }
            Item("Show log", icon = painterResource("images/icon/ic_log.xml")) {
                val logFilePath = Path.of("temporary/logs/app.log")
                viewModel.openTextFile(logFilePath)
            }
        }
    }
}

private fun existsArticle(title: String, folder: Path) =
    Files.list(folder).anyMatch { it.nameWithoutExtension == title }
