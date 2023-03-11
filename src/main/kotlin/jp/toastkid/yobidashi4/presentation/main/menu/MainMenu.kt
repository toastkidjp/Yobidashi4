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
import java.nio.file.Paths
import java.util.stream.Collectors
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.CalendarTab
import jp.toastkid.yobidashi4.domain.model.tab.CompoundInterestCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.ConverterToolTab
import jp.toastkid.yobidashi4.domain.model.tab.EditorSettingTab
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.FileRenameToolTab
import jp.toastkid.yobidashi4.domain.model.tab.FileTab
import jp.toastkid.yobidashi4.domain.model.tab.LoanCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.NumberPlaceGameTab
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.user_agent.UserAgent
import jp.toastkid.yobidashi4.domain.service.archive.ZipArchiver
import jp.toastkid.yobidashi4.domain.service.media.MediaFileFinder
import jp.toastkid.yobidashi4.presentation.editor.legacy.MenuCommand
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.nameWithoutExtension
import kotlin.math.min
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun FrameWindowScope.MainMenu(exitApplication: () -> Unit) {
    val koin = object: KoinComponent {
        val viewModel: MainViewModel by inject()
        val setting: Setting by inject()
        val articleFactory: ArticleFactory by inject()
    }
    val viewModel = koin.viewModel
    val setting = koin.setting

    MenuBar {
        Menu("File") {
            Item("Make new", shortcut = KeyShortcut(Key.N, ctrl = true)) {
                viewModel.setInputBoxAction { input ->
                    if (existsArticle(input, setting.articleFolderPath())) {
                        return@setInputBoxAction
                    }

                    val article = koin.articleFactory.withTitle(input)
                    article.makeFile { "# ${article.getTitle()}" }
                    viewModel.addNewArticle(article.path())
                }
                viewModel.setShowInputBox(true)
            }

            Item(
                if (viewModel.openArticleList()) "Close article list" else "Show article list",
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
            Item("Dump all") {
                val paths = Files.list(setting.articleFolderPath())
                    .sorted { p1, p2 -> Files.getLastModifiedTime(p1).compareTo(Files.getLastModifiedTime(p2)) * -1 }
                    .filter {
                        val name = it.fileName.toString()
                        name.startsWith("20") || name.startsWith("『")
                    }
                    .collect(Collectors.toList())
                ZipArchiver().invoke(paths)
                Desktop.getDesktop().open(File("."))
            }
            Item("Open article folder", shortcut = KeyShortcut(Key.O, alt = true)) {
                Desktop.getDesktop().open(setting.articleFolderPath().toFile())
            }
            Item("Open user folder", shortcut = KeyShortcut(Key.U, alt = true)) {
                Desktop.getDesktop().open(Paths.get("user").toFile())
            }
            Item("Exit") {
                exitApplication()
            }
        }
        val currentTab = viewModel.currentTab()
        if (currentTab is EditorTab) {
            Menu("Edit") {
                Item("Show preview", shortcut = KeyShortcut(Key.M, ctrl = true)) {
                    currentTab.switchPreview()
                }
                Item("Save") {
                    viewModel.emitEditorCommand(MenuCommand.SAVE)
                }
                Item("Editor's Color & Font") {
                    viewModel.openTab(EditorSettingTab())
                }
            }
        }

        Menu("Tab") {
            Item("Close tab", shortcut = KeyShortcut(Key.W, ctrl = true)) {
                if (viewModel.tabs.size != 0) {
                    viewModel.closeCurrent()
                    return@Item
                }
                exitApplication()
            }

            if (viewModel.tabs.size > 1) {
                Item("Find in page", shortcut = KeyShortcut(Key.F, ctrl = true)) {
                    viewModel.switchFind()
                }

                Item("Move previous tab", shortcut = KeyShortcut(Key.PageUp, ctrl = true)) {
                    if (viewModel.tabs.isEmpty()) {
                        return@Item
                    }

                    val nextIndex = if (viewModel.selected.value == 0) viewModel.tabs.size - 1 else viewModel.selected.value - 1
                    viewModel.setSelectedIndex(nextIndex)
                }

                Item("Move next tab", shortcut = KeyShortcut(Key.PageDown, ctrl = true)) {
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
            Item("Web search", shortcut = KeyShortcut(Key.S, alt = true), icon = painterResource("images/icon/ic_search.xml")) {
                viewModel.setShowWebSearch(viewModel.showWebSearch().not())
            }
            Item("What happened today") {
                viewModel.openUrl("https://kids.yahoo.co.jp/today/", false)
            }
            Item("Converter") {
                viewModel.openTab(ConverterToolTab())
            }
            Item("File rename", icon = painterResource("images/icon/ic_rename.xml")) {
                viewModel.openTab(FileRenameToolTab())
            }
            Item("Compound interest calculator") {
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

        if (viewModel.currentTab() is WebTab) {
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
        }

        Menu("Window") {
            Item(viewModel.toggleFullscreenLabel(), shortcut = KeyShortcut(key = Key.F11), icon = painterResource("images/icon/ic_fullscreen.xml")) {
                viewModel.toggleFullscreen()
            }
            Item("Narrow window") {
                viewModel.toggleNarrowWindow()
            }
            Item("Re-lottery background", shortcut = KeyShortcut(key = Key.B, alt = true, ctrl = true)) {
                viewModel.loadBackgroundImage()
            }
        }

        Menu("Setting") {
            Item("Switch dark mode", shortcut = KeyShortcut(key = Key.D, alt = true)) {
                viewModel.switchDarkMode()
            }
            Item("Show log") {
                val logFilePath = Paths.get("data/logs/app.log")
                viewModel.openTextFile(logFilePath)
            }
        }
    }
}

private fun existsArticle(title: String, folder: Path) =
    Files.list(folder).anyMatch { it.nameWithoutExtension == title }
