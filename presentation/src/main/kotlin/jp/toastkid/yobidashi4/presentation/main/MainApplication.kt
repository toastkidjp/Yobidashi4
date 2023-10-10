package jp.toastkid.yobidashi4.presentation.main

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.ContextMenuState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.LocalTextContextMenu
import androidx.compose.foundation.text.TextContextMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLocalization
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import javax.swing.JPopupMenu
import javax.swing.UIManager
import jp.toastkid.yobidashi4.domain.service.text.TextCountMessageFactory
import jp.toastkid.yobidashi4.main.AppTheme
import jp.toastkid.yobidashi4.presentation.main.drop.DropTargetFactory
import jp.toastkid.yobidashi4.presentation.main.drop.TextFileReceiver
import jp.toastkid.yobidashi4.presentation.main.menu.MainMenu
import jp.toastkid.yobidashi4.presentation.slideshow.SlideshowWindow
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory

@OptIn(ExperimentalFoundationApi::class)
fun launchMainApplication() {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    JPopupMenu.setDefaultLightWeightPopupEnabled(false)

    application {
        val mainViewModel = object : KoinComponent { val viewModel: MainViewModel by inject() }.viewModel

        AppTheme(darkTheme = mainViewModel.darkMode()) {
            Window(
                onCloseRequest = {
                    exitApplication()
                },
                title = "Yobidashi 4",
                state = mainViewModel.windowState(),
                icon = painterResource("images/icon.png")
            ) {
                MainMenu { exitApplication() }

                CompositionLocalProvider(
                    LocalTextContextMenu provides object : TextContextMenu {
                        @Composable
                        override fun Area(textManager: TextContextMenu.TextManager, state: ContextMenuState, content: @Composable () -> Unit) {
                            val localization = LocalLocalization.current
                            mainViewModel.setTextManager(textManager)
                            val items = {
                                listOfNotNull(
                                    textManager.cut?.let {
                                        ContextMenuItem(localization.cut, it)
                                    },
                                    textManager.copy?.let {
                                        ContextMenuItem(localization.copy, it)
                                    },
                                    textManager.paste?.let {
                                        ContextMenuItem(localization.paste, it)
                                    },
                                    textManager.selectAll?.let {
                                        ContextMenuItem(localization.selectAll, it)
                                    },
                                    ContextMenuItem("Search") {
                                        object : KoinComponent { val vm: MainViewModel by inject() }.vm
                                            .openUrl("https://search.yahoo.co.jp/search?p=${textManager.selectedText.text}", false)
                                    },
                                    ContextMenuItem("Count", {
                                        object : KoinComponent { val vm: MainViewModel by inject() }.vm
                                            .showSnackbar(TextCountMessageFactory().invoke(textManager.selectedText.text))
                                    })
                                )
                            }

                            ContextMenuArea(items, state, content = content)
                        }
                    }
                ) {
                    MainScaffold()
                }

                window.dropTarget = DropTargetFactory().invoke { mainViewModel.emitDroppedPath(it) }
                TextFileReceiver().launch()
            }

            mainViewModel.slideshowPath()?.let { path ->
                SlideshowWindow().openWindow(path, mainViewModel::closeSlideshow)
            }
        }

        LaunchedEffect(Unit) {
            Thread.setDefaultUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
                override fun uncaughtException(t: Thread?, e: Throwable?) {
                    LoggerFactory.getLogger(javaClass).error(t?.name, e)
                }
            })

            withContext(Dispatchers.IO) {
                mainViewModel.loadBackgroundImage()
            }
        }
    }
}