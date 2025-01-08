package jp.toastkid.yobidashi4.presentation.main.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import jp.toastkid.yobidashi4.domain.model.web.user_agent.UserAgent
import jp.toastkid.yobidashi4.domain.service.notification.ScheduledNotification
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_aggregation
import jp.toastkid.yobidashi4.library.resources.ic_article_folder
import jp.toastkid.yobidashi4.library.resources.ic_back
import jp.toastkid.yobidashi4.library.resources.ic_barcode
import jp.toastkid.yobidashi4.library.resources.ic_bookmark
import jp.toastkid.yobidashi4.library.resources.ic_calendar
import jp.toastkid.yobidashi4.library.resources.ic_chat
import jp.toastkid.yobidashi4.library.resources.ic_clean
import jp.toastkid.yobidashi4.library.resources.ic_clipboard
import jp.toastkid.yobidashi4.library.resources.ic_close_other_tabs
import jp.toastkid.yobidashi4.library.resources.ic_converter
import jp.toastkid.yobidashi4.library.resources.ic_dark_mode
import jp.toastkid.yobidashi4.library.resources.ic_dump
import jp.toastkid.yobidashi4.library.resources.ic_edit
import jp.toastkid.yobidashi4.library.resources.ic_elevation
import jp.toastkid.yobidashi4.library.resources.ic_exit
import jp.toastkid.yobidashi4.library.resources.ic_export
import jp.toastkid.yobidashi4.library.resources.ic_find_in_page
import jp.toastkid.yobidashi4.library.resources.ic_forward
import jp.toastkid.yobidashi4.library.resources.ic_fullscreen
import jp.toastkid.yobidashi4.library.resources.ic_history
import jp.toastkid.yobidashi4.library.resources.ic_home
import jp.toastkid.yobidashi4.library.resources.ic_list
import jp.toastkid.yobidashi4.library.resources.ic_log
import jp.toastkid.yobidashi4.library.resources.ic_lottery_background
import jp.toastkid.yobidashi4.library.resources.ic_markdown
import jp.toastkid.yobidashi4.library.resources.ic_memory
import jp.toastkid.yobidashi4.library.resources.ic_music
import jp.toastkid.yobidashi4.library.resources.ic_narrow_window
import jp.toastkid.yobidashi4.library.resources.ic_new_article
import jp.toastkid.yobidashi4.library.resources.ic_number_place
import jp.toastkid.yobidashi4.library.resources.ic_reload
import jp.toastkid.yobidashi4.library.resources.ic_rename
import jp.toastkid.yobidashi4.library.resources.ic_replace
import jp.toastkid.yobidashi4.library.resources.ic_restart
import jp.toastkid.yobidashi4.library.resources.ic_save
import jp.toastkid.yobidashi4.library.resources.ic_search
import jp.toastkid.yobidashi4.library.resources.ic_shuffle
import jp.toastkid.yobidashi4.library.resources.ic_slideshow
import jp.toastkid.yobidashi4.library.resources.ic_tab_close
import jp.toastkid.yobidashi4.library.resources.ic_user_folder
import jp.toastkid.yobidashi4.library.resources.ic_user_template
import jp.toastkid.yobidashi4.library.resources.ic_wallpaper
import jp.toastkid.yobidashi4.library.resources.ic_world_time
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.min

@Composable
fun FrameWindowScope.MainMenu(exitApplication: () -> Unit) {
    val viewModel = remember { MainMenuViewModel() }

    MenuBar {
        Menu("File") {
            Item(
                "Make new",
                shortcut = KeyShortcut(Key.N, ctrl = true),
                icon = painterResource(Res.drawable.ic_new_article),
                onClick = viewModel::makeNewArticle
            )

            Item(
                viewModel.switchArticleListLabel(),
                icon = painterResource(viewModel.switchArticleListIconPath()),
                shortcut = viewModel.switchArticleListShortcut(),
                onClick = viewModel::switchArticleList
            )

            Item(
                "Find",
                icon = painterResource(Res.drawable.ic_search),
                shortcut = KeyShortcut(Key.F, alt = true),
                onClick = viewModel::switchFindArticle
            )

            Item(
                "Update finder index",
                icon = painterResource(Res.drawable.ic_reload),
                onClick = viewModel::updateFinderIndex
            )

            Item(
                "Dump latest",
                icon = painterResource(Res.drawable.ic_dump),
                onClick = viewModel::dumpLatest
            )

            Item("Dump all", onClick = viewModel::dumpAll)

            Item(
                "Open article folder",
                icon = painterResource(Res.drawable.ic_article_folder),
                shortcut = KeyShortcut(Key.O, alt = true),
                onClick = viewModel::openArticleFolder
            )

            Item(
                "Open user folder",
                icon = painterResource(Res.drawable.ic_user_folder),
                shortcut = KeyShortcut(Key.U, alt = true),
                onClick = viewModel::openUserFolder
            )

            Item(
                "Open download folder",
                icon = painterResource(Res.drawable.ic_user_folder),
                shortcut = KeyShortcut(Key.U, alt = true),
                onClick = viewModel::openDownloadFolder
            )

            Item(
                "Exit",
                icon = painterResource(Res.drawable.ic_exit),
                onClick = exitApplication
            )
        }

        if (viewModel.useEditorMenu()) {
            Menu("Edit") {
                Item(
                    viewModel.switchPreviewLabel(),
                    shortcut = KeyShortcut(Key.M, ctrl = true),
                    icon = painterResource(Res.drawable.ic_markdown),
                    onClick = viewModel::switchPreview
                )

                Item(
                    "Save",
                    shortcut = KeyShortcut(Key.S, ctrl = true),
                    icon = painterResource(Res.drawable.ic_save),
                    onClick = viewModel::saveCurrentEditorTab
                )

                Item(
                    "Save all",
                    shortcut = KeyShortcut(Key.S, ctrl = true, shift = true),
                    icon = painterResource(Res.drawable.ic_save),
                    onClick = viewModel::saveAllEditorTab
                )

                Item(
                    "Replace",
                    shortcut = KeyShortcut(Key.R, ctrl = true),
                    icon = painterResource(Res.drawable.ic_replace),
                    onClick = viewModel::switchFind
                )

                Item(
                    "Editor's Color & Font",
                    onClick = viewModel::openEditorSetting
                )
            }
        }

        Menu("Tab") {
            Item("Close tab", shortcut = KeyShortcut(Key.W, ctrl = true), icon = painterResource(Res.drawable.ic_tab_close)) {
                viewModel.closeCurrentTab(exitApplication)
            }

            if (viewModel.useAdditionalTabMenu()) {
                Item(
                    "Close all tabs",
                    shortcut = KeyShortcut(Key.W, alt = true),
                    icon = painterResource(Res.drawable.ic_clean),
                    onClick = viewModel::closeAllTabs
                )

                Item(
                    "Close other tabs",
                    icon = painterResource(Res.drawable.ic_close_other_tabs),
                    onClick = viewModel::closeOtherTabs
                )

                Item(
                    "Copy tab's title",
                    icon = painterResource(Res.drawable.ic_clipboard),
                    onClick = viewModel::copyTabsTitle
                )

                if (viewModel.currentIsWebTab()) {
                    Item(
                        "Copy tab's URL",
                        icon = painterResource(Res.drawable.ic_clipboard),
                        onClick = viewModel::copyTabsUrl
                    )

                    Item(
                        "Copy tab's markdown link",
                        icon = painterResource(Res.drawable.ic_clipboard),
                        onClick = viewModel::copyTabsUrlAsMarkdownLink
                    )

                    Item(
                        "Add bookmark",
                        onClick = viewModel::addWebBookmark
                    )
                }
            }

            viewModel.findSlideshowPath()?.let { slideshowSourcePath ->
                Item("Slideshow", shortcut = KeyShortcut(Key.F5), icon = painterResource(Res.drawable.ic_slideshow)) {
                    viewModel.slideshow(slideshowSourcePath)
                }
            }

            if (viewModel.currentIsEditableTab()) {
                Item(
                    "Edit",
                    shortcut = KeyShortcut(Key.E, ctrl = true),
                    icon = painterResource(Res.drawable.ic_edit),
                    onClick = viewModel::openEditorTabWithCurrentTabsPath
                )
            }

            Item(
                "Find in page",
                icon = painterResource(Res.drawable.ic_find_in_page),
                shortcut = KeyShortcut(Key.F, ctrl = true),
                onClick = viewModel::switchFind
            )

            if (viewModel.canMoveTab()) {
                Item("Move previous tab", icon = painterResource(Res.drawable.ic_back), shortcut = KeyShortcut(Key.PageUp, ctrl = true)) {
                    viewModel.moveTabIndex(-1)
                }

                Item("Move next tab", icon = painterResource(Res.drawable.ic_forward), shortcut = KeyShortcut(Key.PageDown, ctrl = true)) {
                    viewModel.moveTabIndex(1)
                }
            }

            Item(
                "Search with selected text",
                icon = painterResource(Res.drawable.ic_search),
                shortcut = KeyShortcut(Key.O, ctrl = true, shift = true),
                onClick = viewModel::searchWithSelectedText
            )

            (1 .. min(10, viewModel.tabCount())).forEach {
                Item("Tab $it", shortcut = viewModel.makeTabIndexShortcut(it)) {
                    viewModel.setSelectedIndex(it - 1)
                }
            }
        }

        Menu("Tool") {
            Item(
                "Assistant Chat",
                shortcut = KeyShortcut(Key.G, alt = true),
                icon = painterResource(Res.drawable.ic_chat),
                onClick = viewModel::openChatTab
            )

            Item(
                "Bookmark",
                shortcut = KeyShortcut(Key.B, alt = true),
                icon = painterResource(Res.drawable.ic_bookmark),
                onClick = viewModel::openBookmarkTab
            )

            Item(
                "Calendar",
                shortcut = KeyShortcut(Key.C, alt = true),
                icon = painterResource(Res.drawable.ic_calendar),
                onClick = viewModel::openCalendarTab
            )

            Item(
                "Aggregation",
                shortcut = KeyShortcut(Key.A, alt = true),
                icon = painterResource(Res.drawable.ic_aggregation),
                onClick = viewModel::openAggregationBox
            )

            Item(
                "Web history",
                shortcut = KeyShortcut(Key.H, alt = true),
                icon = painterResource(Res.drawable.ic_history),
                onClick = viewModel::openWebHistoryTab
            )

            Item(
                "Web search",
                shortcut = KeyShortcut(Key.S, alt = true),
                icon = painterResource(Res.drawable.ic_search),
                onClick =  viewModel::switchWebSearchBox
            )

            Item(
                "Barcode tool",
                icon = painterResource(Res.drawable.ic_barcode),
                onClick = viewModel::openBarcodeToolTab
            )

            Item(
                "Converter",
                icon = painterResource(Res.drawable.ic_converter),
                onClick = viewModel::openConverterToolTab
            )

            Item(
                "File rename",
                icon = painterResource(Res.drawable.ic_rename),
                onClick = viewModel::openFileRenameToolTab
            )

            Item(
                "Roulette",
                shortcut = KeyShortcut(Key.R, alt = true),
                icon = painterResource(Res.drawable.ic_shuffle),
                onClick = viewModel::openRouletteToolTab
            )

            Item(
                "Compound interest calculator",
                icon = painterResource(Res.drawable.ic_elevation),
                onClick = viewModel::openCompoundInterestCalculatorTab
            )

            Item(
                "Loan calculator",
                shortcut = KeyShortcut(Key.L, alt = true),
                icon = painterResource(Res.drawable.ic_home),
                onClick = viewModel::openLoanCalculatorTab
            )

            Item(
                "Number place",
                shortcut = KeyShortcut(Key.N, alt = true),
                icon = painterResource(Res.drawable.ic_number_place),
                onClick = viewModel::openNumberPlaceTab
            )

            Item(
                "Music player",
                shortcut = KeyShortcut(Key.M, alt = true),
                icon = painterResource(Res.drawable.ic_music),
                onClick = viewModel::openMusicPlayerTab
            )

            Item(
                "World time",
                shortcut = viewModel.toggleWorldTimeShortcut(),
                icon = painterResource(Res.drawable.ic_world_time),
                onClick = viewModel::toggleWorldTime
            )
        }

        Menu("User agent") {
            UserAgent.entries.forEach {
                RadioButtonItem(it.title(), selected = viewModel.isSelectedUserAgent(it)) {
                    viewModel.chooseUserAgent(it)
                }
            }
        }

        Menu("Notification") {
            Item(
                "List",
                icon = painterResource(Res.drawable.ic_list),
                onClick = viewModel::openNotificationList
            )

            Item(
                "Restart",
                icon = painterResource(Res.drawable.ic_restart)
            ) {
                CoroutineScope(Dispatchers.IO).launch {
                    object : KoinComponent { val notification: ScheduledNotification by inject() }.notification.start()
                }
            }

            Item(
                "Open file",
                icon = painterResource(Res.drawable.ic_user_template),
                onClick = viewModel::openNotificationFile
            )

            Item(
                "Export",
                icon = painterResource(Res.drawable.ic_export),
                onClick = viewModel::exportNotifications
            )
        }

        Menu("Window") {
            Item(
                viewModel.toggleFullscreenLabel(),
                shortcut = KeyShortcut(key = Key.F11),
                icon = painterResource(Res.drawable.ic_fullscreen),
                onClick = viewModel::toggleFullscreen
            )

            Item(
                "Narrow window",
                icon = painterResource(Res.drawable.ic_narrow_window),
                onClick = viewModel::toggleNarrowWindow
            )

            Item("Default window size", onClick = viewModel::toDefaultWindowSize)

            Item(
                "Re-lottery background",
                shortcut = KeyShortcut(key = Key.B, alt = true, ctrl = true),
                icon = painterResource(Res.drawable.ic_lottery_background),
                onClick = viewModel::loadBackgroundImage
            )

            Item(
                "Use background",
                shortcut = KeyShortcut(key = Key.B, alt = true, shift = true),
                icon = painterResource(Res.drawable.ic_wallpaper),
                onClick = viewModel::switchUseBackground
            )
        }

        Menu("Setting") {
            Item(
                "Switch dark mode",
                icon = painterResource(Res.drawable.ic_dark_mode),
                shortcut = KeyShortcut(key = Key.D, alt = true),
                onClick = viewModel::switchDarkMode
            )

            Item(
                "Open article template",
                icon = painterResource(Res.drawable.ic_user_template),
                onClick = viewModel::openArticleTemplate
            )

            Item(
                "Open properties",
                onClick = viewModel::openProperty
            )

            Item(
                viewModel.switchMemoryUsageBoxLabel(),
                icon = painterResource(Res.drawable.ic_memory),
                onClick = viewModel::switchMemoryUsageBox
            )

            Item(
                "Show log",
                icon = painterResource(Res.drawable.ic_log),
                onClick = viewModel::openLogViewerTab
            )
        }
    }
}
