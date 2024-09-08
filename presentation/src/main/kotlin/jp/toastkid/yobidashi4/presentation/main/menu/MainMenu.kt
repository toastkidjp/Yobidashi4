package jp.toastkid.yobidashi4.presentation.main.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import jp.toastkid.yobidashi4.domain.model.web.user_agent.UserAgent
import jp.toastkid.yobidashi4.domain.service.notification.ScheduledNotification
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun FrameWindowScope.MainMenu(exitApplication: () -> Unit) {
    val viewModel = remember { MainMenuViewModel() }

    MenuBar {
        Menu("File") {
            Item(
                "Make new",
                shortcut = KeyShortcut(Key.N, ctrl = true),
                icon = painterResource("images/icon/ic_new_article.xml"),
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
                icon = painterResource("images/icon/ic_search.xml"),
                shortcut = KeyShortcut(Key.F, alt = true),
                onClick = viewModel::switchFindArticle
            )

            Item(
                "Update finder index",
                icon = painterResource("images/icon/ic_reload.xml"),
                onClick = viewModel::updateFinderIndex
            )

            Item(
                "Dump latest",
                icon = painterResource("images/icon/ic_dump.xml"),
                onClick = viewModel::dumpLatest
            )

            Item("Dump all", onClick = viewModel::dumpAll)

            Item(
                "Open article folder",
                icon = painterResource("images/icon/ic_article_folder.xml"),
                shortcut = KeyShortcut(Key.O, alt = true),
                onClick = viewModel::openArticleFolder
            )

            Item(
                "Open user folder",
                icon = painterResource("images/icon/ic_user_folder.xml"),
                shortcut = KeyShortcut(Key.U, alt = true),
                onClick = viewModel::openUserFolder
            )

            Item(
                "Open download folder",
                icon = painterResource("images/icon/ic_user_folder.xml"),
                shortcut = KeyShortcut(Key.U, alt = true),
                onClick = viewModel::openDownloadFolder
            )

            Item(
                "Exit",
                icon = painterResource("images/icon/ic_exit.xml"),
                onClick = exitApplication
            )
        }

        if (viewModel.useEditorMenu()) {
            Menu("Edit") {
                Item(
                    viewModel.switchPreviewLabel(),
                    shortcut = KeyShortcut(Key.M, ctrl = true),
                    icon = painterResource("images/icon/ic_markdown.xml"),
                    onClick = viewModel::switchPreview
                )

                Item(
                    "Save",
                    shortcut = KeyShortcut(Key.S, ctrl = true),
                    icon = painterResource("images/icon/ic_save.xml"),
                    onClick = viewModel::saveCurrentEditorTab
                )

                Item(
                    "Save all",
                    shortcut = KeyShortcut(Key.S, ctrl = true, shift = true),
                    icon = painterResource("images/icon/ic_save.xml"),
                    onClick = viewModel::saveAllEditorTab
                )

                Item(
                    "Replace",
                    shortcut = KeyShortcut(Key.R, ctrl = true),
                    icon = painterResource("images/icon/ic_replace.xml"),
                    onClick = viewModel::switchFind
                )

                Item(
                    "Editor's Color & Font",
                    onClick = viewModel::openEditorSetting
                )
            }
        }

        Menu("Tab") {
            Item("Close tab", shortcut = KeyShortcut(Key.W, ctrl = true), icon = painterResource("images/icon/ic_tab_close.xml")) {
                viewModel.closeCurrentTab(exitApplication)
            }

            if (viewModel.useAdditionalTabMenu()) {
                Item(
                    "Close all tabs",
                    shortcut = KeyShortcut(Key.W, alt = true),
                    icon = painterResource("images/icon/ic_clean.xml"),
                    onClick = viewModel::closeAllTabs
                )

                Item(
                    "Close other tabs",
                    icon = painterResource("images/icon/ic_close_other_tabs.xml"),
                    onClick = viewModel::closeOtherTabs
                )

                Item("Copy tab's title", icon = painterResource("images/icon/ic_clipboard.xml")) {
                    viewModel.copyTabsTitle()
                }

                if (viewModel.currentIsWebTab()) {
                    Item("Copy tab's URL", icon = painterResource("images/icon/ic_clipboard.xml")) {
                        viewModel.copyTabsUrl()
                    }

                    Item(
                        "Copy tab's markdown link",
                        icon = painterResource("images/icon/ic_clipboard.xml"),
                        onClick = viewModel::copyTabsUrlAsMarkdownLink
                    )

                    Item(
                        "Add bookmark",
                        onClick = viewModel::addWebBookmark
                    )
                }
            }

            viewModel.findSlideshowPath()?.let { slideshowSourcePath ->
                Item("Slideshow", shortcut = KeyShortcut(Key.F5), icon = painterResource("images/icon/ic_slideshow.xml")) {
                    viewModel.slideshow(slideshowSourcePath)
                }
            }

            if (viewModel.currentIsEditableTab()) {
                Item("Edit", shortcut = KeyShortcut(Key.E, ctrl = true), icon = painterResource("images/icon/ic_edit.xml")) {
                    viewModel.openEditorTabWithCurrentTabsPath()
                }
            }

            Item(
                "Find in page",
                icon = painterResource("images/icon/ic_search.xml"),
                shortcut = KeyShortcut(Key.F, ctrl = true),
                onClick = viewModel::switchFind
            )

            if (viewModel.canMoveTab()) {
                Item("Move previous tab", icon = painterResource("images/icon/ic_back.xml"), shortcut = KeyShortcut(Key.PageUp, ctrl = true)) {
                    viewModel.moveTabIndex(-1)
                }

                Item("Move next tab", icon = painterResource("images/icon/ic_forward.xml"), shortcut = KeyShortcut(Key.PageDown, ctrl = true)) {
                    viewModel.moveTabIndex(1)
                }
            }

            Item(
                "Search with selected text",
                icon = painterResource("images/icon/ic_search.xml"),
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
            Item("Assistant Chat", shortcut = KeyShortcut(Key.G, alt = true), icon = painterResource("images/icon/ic_chat.xml")) {
                viewModel.openChatTab()
            }
            Item("Bookmark", shortcut = KeyShortcut(Key.B, alt = true), icon = painterResource("images/icon/ic_bookmark.xml")) {
                viewModel.openBookmarkTab()
            }
            Item("Calendar", shortcut = KeyShortcut(Key.C, alt = true), icon = painterResource("images/icon/ic_calendar.xml")) {
                viewModel.openCalendarTab()
            }
            Item("Aggregation", shortcut = KeyShortcut(Key.A, alt = true), icon = painterResource("images/icon/ic_aggregation.xml")) {
                viewModel.openAggregationBox()
            }

            Item(
                "Web history",
                shortcut = KeyShortcut(Key.H, alt = true),
                icon = painterResource("images/icon/ic_history.xml"),
                onClick = viewModel::openWebHistoryTab
            )

            Item(
                "Web search",
                shortcut = KeyShortcut(Key.S, alt = true),
                icon = painterResource("images/icon/ic_search.xml"),
                onClick =  viewModel::switchWebSearchBox
            )

            Item(
                "Barcode tool",
                icon = painterResource("images/icon/ic_barcode.xml"),
                onClick = viewModel::openBarcodeToolTab
            )

            Item(
                "Converter",
                icon = painterResource("images/icon/ic_converter.xml"),
                onClick = viewModel::openConverterToolTab
            )

            Item(
                "File rename",
                icon = painterResource("images/icon/ic_rename.xml"),
                onClick = viewModel::openFileRenameToolTab
            )

            Item(
                "Roulette",
                shortcut = KeyShortcut(Key.R, alt = true),
                icon = painterResource("images/icon/ic_shuffle.xml"),
                onClick = viewModel::openRouletteToolTab
            )

            Item(
                "Compound interest calculator",
                icon = painterResource("images/icon/ic_elevation.xml"),
                onClick = viewModel::openCompoundInterestCalculatorTab
            )

            Item(
                "Loan calculator",
                shortcut = KeyShortcut(Key.L, alt = true),
                icon = painterResource("images/icon/ic_home.xml"),
                onClick = viewModel::openLoanCalculatorTab
            )

            Item(
                "Number place",
                shortcut = KeyShortcut(Key.N, alt = true),
                icon = painterResource("images/icon/ic_number_place.xml"),
                onClick = viewModel::openNumberPlaceTab
            )

            Item(
                "Music player",
                shortcut = KeyShortcut(Key.M, alt = true),
                icon = painterResource("images/icon/ic_music.xml"),
                onClick = viewModel::openMusicPlayerTab
            )

            Item(
                "World time",
                shortcut = viewModel.toggleWorldTimeShortcut(),
                icon = painterResource("images/icon/ic_world_time.xml"),
                onClick = viewModel::toggleWorldTime
            )
        }

        Menu("User agent") {
            UserAgent.values().forEach {
                RadioButtonItem(it.title(), selected = viewModel.isSelectedUserAgent(it)) {
                    viewModel.chooseUserAgent(it)
                }
            }
        }

        Menu("Notification") {
            Item(
                "List",
                onClick = viewModel::openNotificationList
            )

            Item("Restart") {
                CoroutineScope(Dispatchers.IO).launch {
                    object : KoinComponent { val notification: ScheduledNotification by inject() }.notification.start()
                }
            }

            Item(
                "Open file",
                icon = painterResource("images/icon/ic_user_template.xml"),
                onClick = viewModel::openNotificationFile
            )

            Item(
                "Export",
                icon = painterResource("images/icon/ic_export.xml"),
                onClick = viewModel::exportNotifications
            )
        }

        Menu("Window") {
            Item(
                viewModel.toggleFullscreenLabel(),
                shortcut = KeyShortcut(key = Key.F11),
                icon = painterResource("images/icon/ic_fullscreen.xml"),
                onClick = viewModel::toggleFullscreen
            )

            Item(
                "Narrow window",
                icon = painterResource("images/icon/ic_narrow_window.xml"),
                onClick = viewModel::toggleNarrowWindow
            )

            Item("Default window size", onClick = viewModel::toDefaultWindowSize)

            Item(
                "Re-lottery background",
                shortcut = KeyShortcut(key = Key.B, alt = true, ctrl = true),
                icon = painterResource("images/icon/ic_lottery_background.xml"),
                onClick = viewModel::loadBackgroundImage
            )

            Item(
                "Use background",
                shortcut = KeyShortcut(key = Key.B, alt = true, shift = true),
                icon = painterResource("images/icon/ic_wallpaper.xml"),
                onClick = viewModel::switchUseBackground
            )
        }

        Menu("Setting") {
            Item(
                "Switch dark mode",
                icon = painterResource("images/icon/ic_dark_mode.xml"),
                shortcut = KeyShortcut(key = Key.D, alt = true),
                onClick = viewModel::switchDarkMode
            )

            Item(
                "Open article template",
                icon = painterResource("images/icon/ic_user_template.xml"),
                onClick = viewModel::openArticleTemplate
            )

            Item(
                viewModel.switchMemoryUsageBoxLabel(),
                icon = painterResource("images/icon/ic_memory.xml"),
                onClick = viewModel::switchMemoryUsageBox
            )

            Item(
                "Show log",
                icon = painterResource("images/icon/ic_log.xml"),
                onClick = viewModel::openLogViewerTab
            )
        }
    }
}
