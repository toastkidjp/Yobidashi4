package jp.toastkid.yobidashi4.presentation.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.LocalTextContextMenu
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import javax.swing.JPopupMenu
import javax.swing.UIManager
import jp.toastkid.yobidashi4.domain.service.notification.ScheduledNotification
import jp.toastkid.yobidashi4.presentation.main.drop.DropTargetFactory
import jp.toastkid.yobidashi4.presentation.main.drop.TextFileReceiver
import jp.toastkid.yobidashi4.presentation.main.menu.MainMenu
import jp.toastkid.yobidashi4.presentation.main.menu.TextContextMenuFactory
import jp.toastkid.yobidashi4.presentation.main.theme.AppTheme
import jp.toastkid.yobidashi4.presentation.main.title.LauncherJarTimestampReader
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
        val mainViewModel = remember { object : KoinComponent { val viewModel: MainViewModel by inject() }.viewModel }
        val trayState = rememberTrayState()

        AppTheme(darkTheme = mainViewModel.darkMode()) {
            Tray(
                state = trayState,
                icon = painterResource("images/icon.png"),
                menu = {
                    Item(
                        "Exit",
                        onClick = {
                            exitApplication()
                        }
                    )
                }
            )

            Window(
                onCloseRequest = {
                    exitApplication()
                },
                title = "Yobidashi 4 ${LauncherJarTimestampReader().invoke() ?: ""}",
                state = mainViewModel.windowState(),
                icon = painterResource("images/icon.png")
            ) {
                MainMenu { exitApplication() }

                CompositionLocalProvider(
                    LocalTextContextMenu provides TextContextMenuFactory(mainViewModel).invoke()
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

        val notification = object : KoinComponent { val notification: ScheduledNotification by inject() }.notification

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                notification.start()
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

            withContext(Dispatchers.IO) {
                notification
                    .notificationFlow()
                    .collect {
                        println("receive ${it.date}")
                        trayState.sendNotification(Notification(it.title, it.text, Notification.Type.Info))
                    }
            }
        }
    }
}
