package jp.toastkid.yobidashi4.presentation.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.LocalTextContextMenu
import androidx.compose.foundation.text.TextContextMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import javax.swing.JPopupMenu
import javax.swing.UIManager
import jp.toastkid.yobidashi4.domain.service.notification.ScheduledNotification
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.icon
import jp.toastkid.yobidashi4.presentation.main.content.MainScaffold
import jp.toastkid.yobidashi4.presentation.main.drop.DropTargetFactory
import jp.toastkid.yobidashi4.presentation.main.menu.MainMenu
import jp.toastkid.yobidashi4.presentation.main.menu.TextContextMenuFactory
import jp.toastkid.yobidashi4.presentation.main.theme.AppTheme
import jp.toastkid.yobidashi4.presentation.main.title.LauncherJarTimestampReader
import jp.toastkid.yobidashi4.presentation.main.tray.MainTray
import jp.toastkid.yobidashi4.presentation.slideshow.SlideshowWindow
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalFoundationApi::class)
fun launchMainApplication(exitProcessOnExit: Boolean = true) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    JPopupMenu.setDefaultLightWeightPopupEnabled(false)

    application(exitProcessOnExit) {
        Application(LocalTextContextMenu)
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun ApplicationScope.Application(LocalTextContextMenu: ProvidableCompositionLocal<TextContextMenu>) {
    val mainViewModel = remember { object : KoinComponent { val viewModel: MainViewModel by inject() }.viewModel }

    AppTheme(darkTheme = mainViewModel.darkMode()) {
        MainTray()

        Window(
            onCloseRequest = ::exitApplication,
            title = "Yobidashi 4 ${LauncherJarTimestampReader().invoke() ?: ""}",
            state = mainViewModel.windowState(),
            visible = mainViewModel.windowVisible(),
            icon = painterResource(Res.drawable.icon)
        ) {
            MainMenu(::exitApplication)

            CompositionLocalProvider(
                LocalTextContextMenu provides TextContextMenuFactory(mainViewModel).invoke()
            ) {
                MainScaffold()
            }

            window.dropTarget = DropTargetFactory().invoke(mainViewModel::emitDroppedPath)

            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    mainViewModel.launchDroppedPathFlow()
                }
            }
        }

        mainViewModel.slideshowPath()?.let { path ->
            SlideshowWindow().openWindow(path, mainViewModel::closeSlideshow)
        }
    }

    val notification = object : KoinComponent {
        val notification: ScheduledNotification by inject()
    }.notification

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            notification.start()
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            mainViewModel.loadBackgroundImage()
        }

        withContext(Dispatchers.IO) {
            notification
                .notificationFlow()
                .collect(mainViewModel::sendNotification)
        }
    }

    LaunchedEffect(mainViewModel.windowVisible()) {
        if (mainViewModel.windowVisible().not()) {
            exitApplication()
        }
    }
}
