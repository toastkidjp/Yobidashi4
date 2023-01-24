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
import javax.swing.JOptionPane
import jp.toastkid.yobidashi4.domain.model.article.Article
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.CalendarTab
import jp.toastkid.yobidashi4.domain.model.tab.FileTab
import jp.toastkid.yobidashi4.domain.model.tab.LoanCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.NumberPlaceGameTab
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.web.user_agent.UserAgent
import jp.toastkid.yobidashi4.domain.service.aggregation.AggregationMenuItemGeneratorService
import jp.toastkid.yobidashi4.domain.service.aggregation.ArticleLengthAggregatorService
import jp.toastkid.yobidashi4.domain.service.aggregation.EatingOutCounterService
import jp.toastkid.yobidashi4.domain.service.aggregation.MovieMemoSubtitleExtractor
import jp.toastkid.yobidashi4.domain.service.aggregation.Nikkei225AggregatorService
import jp.toastkid.yobidashi4.domain.service.aggregation.OutgoAggregatorService
import jp.toastkid.yobidashi4.domain.service.aggregation.StepsAggregatorService
import jp.toastkid.yobidashi4.domain.service.aggregation.StocksAggregatorService
import jp.toastkid.yobidashi4.domain.service.archive.ArticleFinderService
import jp.toastkid.yobidashi4.domain.service.archive.ZipArchiver
import jp.toastkid.yobidashi4.domain.service.media.MediaFileFinder
import jp.toastkid.yobidashi4.domain.service.tool.PrivateImageSearchService
import jp.toastkid.yobidashi4.domain.service.tool.compound.CompoundInterestCalculatorMenuGeneratorService
import jp.toastkid.yobidashi4.domain.service.tool.converter.UnixTimeConverterService
import jp.toastkid.yobidashi4.domain.service.tool.converter.UrlEncoderService
import jp.toastkid.yobidashi4.domain.service.tool.rename.FileRenameService
import jp.toastkid.yobidashi4.presentation.editor.legacy.service.AppearanceSettingService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.nameWithoutExtension
import kotlin.math.min
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalComposeUiApi::class)
@Composable()
fun FrameWindowScope.MainMenu(exitApplication: () -> Unit) {
    val koin = object: KoinComponent {
        val viewModel: MainViewModel by inject()
        val setting: Setting by inject()
    }
    val viewModel = koin.viewModel
    val setting = koin.setting

    MenuBar {
        Menu("File") {
            Item("Make new", shortcut = KeyShortcut(Key.N, ctrl = true)) {
                val dialog = JOptionPane.showInputDialog("Please input new article name.")
                if (dialog.isNullOrBlank() || existsArticle(dialog, setting.articleFolderPath())) {
                    return@Item
                }

                val article = Article.withTitle(dialog)
                article.makeFile { "# ${article.getTitle()}" }
                viewModel.addNewArticle(article.path())
            }

            Item(
                if (viewModel.openArticleList()) "Close article list" else "Show article list",
                shortcut = KeyShortcut(if (viewModel.openArticleList()) Key.DirectionLeft else Key.DirectionRight, alt = true)
            ) {
                viewModel.switchArticleList()
            }

            Item("Find", icon = painterResource("images/icon/ic_search.xml"), shortcut = KeyShortcut(Key.F, alt = true)) {
                ArticleFinderService().invoke { title, articles ->
                    viewModel.openFileListTab(title, articles, true, FileTab.Type.FIND)
                }
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
            Item("Open folder", shortcut = KeyShortcut(Key.O, alt = true)) {
                Desktop.getDesktop().open(setting.articleFolderPath().toFile())
            }
            Item("Exit", shortcut = KeyShortcut(Key.E, alt = true)) {
                exitApplication()
            }
        }
        Menu("Aggregation") {
            Item("Movies", shortcut = KeyShortcut(Key.M, ctrl = true)) {
                AggregationMenuItemGeneratorService().invoke(
                    "Movie memo",
                    "Please input year and month you want aggregate movies? ex)",
                    { MovieMemoSubtitleExtractor().invoke(it) },
                    { title, result -> viewModel.openAggregationResultTab(title, result) }
                )
            }
            Item("Stock") {
                AggregationMenuItemGeneratorService().invoke(
                    "Stock",
                    "Please input year and month you want aggregate stocks? ex)",
                    { StocksAggregatorService().invoke(it) },
                    { title, result -> viewModel.openAggregationResultTab(title, result) }
                )
            }
            Item("Outgo", shortcut = KeyShortcut(Key.G, ctrl = true)) {
                AggregationMenuItemGeneratorService().invoke(
                    "OutGo",
                    "Please input year and month you want aggregate outgo?",
                    { OutgoAggregatorService().invoke(it) },
                    { title, result -> viewModel.openAggregationResultTab(title, result) }
                )
            }
            Item("Eat out") {
                AggregationMenuItemGeneratorService().invoke(
                    "Eat out count",
                    "Please input year and month you want count eat-out times?",
                    { EatingOutCounterService().invoke(it) },
                    { title, result -> viewModel.openAggregationResultTab(title, result) }
                )
            }
            Item("Article length", shortcut = KeyShortcut(Key.L, ctrl = true)) {
                AggregationMenuItemGeneratorService().invoke(
                    "Article length",
                    "Please input year and month you want aggregate article length?",
                    { ArticleLengthAggregatorService().invoke(it) },
                    { title, result -> viewModel.openAggregationResultTab(title, result) }
                )
            }
            Item("Steps") {
                AggregationMenuItemGeneratorService().invoke(
                    "Steps",
                    "Please input year and month you want aggregate steps? ex)",
                    { StepsAggregatorService().invoke(it) },
                    { title, result -> viewModel.openAggregationResultTab(title, result) }
                )
            }
            Item("Nikkei 225") {
                AggregationMenuItemGeneratorService().invoke(
                    "Nikkei 225",
                    "Please input year and month you want aggregate Nikkei 225? ex)",
                    { Nikkei225AggregatorService().invoke(it) },
                    { title, result -> viewModel.openAggregationResultTab(title, result) }
                )
            }
        }
        Menu("Tool") {
            Item("Bookmark", shortcut = KeyShortcut(Key.B, alt = true), icon = painterResource("images/icon/ic_bookmark.xml")) {
                viewModel.openTab(WebBookmarkTab())
            }
            Item("Calendar", shortcut = KeyShortcut(Key.C, alt = true), icon = painterResource("images/icon/ic_calendar.xml")) {
                viewModel.openTab(CalendarTab())
            }
            Item("Web search", shortcut = KeyShortcut(Key.S, alt = true), icon = painterResource("images/icon/ic_search.xml")) {
                viewModel.setShowWebSearch(viewModel.showWebSearch.value.not())
            }
            Item("Image search", shortcut = KeyShortcut(Key.P, alt = true)) { PrivateImageSearchService().invoke() }
            Item("What happened today") {
                viewModel.openUrl("https://kids.yahoo.co.jp/today/", false)
            }
            Item("Google Trend") {
                viewModel.openUrl("https://trends.google.co.jp/trends/trendingsearches/realtime", false)
            }
            Item("URL Encode") {
                UrlEncoderService().invoke()
            }
            Item("Unix time conversion") {
                UnixTimeConverterService().invoke()
            }
            Item("File rename") {
                FileRenameService().invoke()
            }
            Item("Compound interest calculator") {
                CompoundInterestCalculatorMenuGeneratorService(
                    resultConsumer = { title, result -> viewModel.openAggregationResultTab(title, result) }
                ).invoke()
            }
            Item("Loan calculator", shortcut = KeyShortcut(Key.L, alt = true)) {
                viewModel.openTab(LoanCalculatorTab())
            }
            Item("Number place", shortcut = KeyShortcut(Key.N, alt = true)) {
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
        Menu("Tab") {
            Item("Close tab", shortcut = KeyShortcut(Key.W, ctrl = true)) {
                if (viewModel.tabs.size != 0) {
                    viewModel.closeCurrent()
                    return@Item
                }
                exitApplication()
            }

            if (viewModel.tabs.size > 1) {
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

        Menu("Setting") {
            Item(viewModel.toggleFullscreenLabel(), shortcut = KeyShortcut(key = Key.F11)) {
                viewModel.toggleFullscreen()
            }
            Item("Switch dark mode", shortcut = KeyShortcut(key = Key.D, alt = true)) {
                viewModel.switchDarkMode()
            }
            Item("Editor's Color & Font") {
                AppearanceSettingService().invoke()
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
