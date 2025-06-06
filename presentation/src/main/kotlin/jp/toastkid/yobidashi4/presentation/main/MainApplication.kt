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
import jp.toastkid.yobidashi4.domain.service.io.IoContextProvider
import jp.toastkid.yobidashi4.domain.service.notification.ScheduledNotification
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.icon
import jp.toastkid.yobidashi4.presentation.main.content.MainScaffold
import jp.toastkid.yobidashi4.presentation.main.menu.MainMenu
import jp.toastkid.yobidashi4.presentation.main.menu.TextContextMenuFactory
import jp.toastkid.yobidashi4.presentation.main.theme.AppTheme
import jp.toastkid.yobidashi4.presentation.main.title.LauncherJarTimestampReader
import jp.toastkid.yobidashi4.presentation.main.tray.MainTray
import jp.toastkid.yobidashi4.presentation.slideshow.SlideshowWindow
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.swing.JPopupMenu
import javax.swing.UIManager

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
private fun ApplicationScope.Application(localTextContextMenu: ProvidableCompositionLocal<TextContextMenu>) {
    val koin = remember {
        object : KoinComponent {
            val viewModel: MainViewModel by inject()
            val ioContextProvider: IoContextProvider by inject()
        }
    }
    val mainViewModel = remember { koin.viewModel }
    val ioContextProvider = remember { koin.ioContextProvider }

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
                localTextContextMenu provides TextContextMenuFactory(mainViewModel).invoke()
            ) {
                MainScaffold()
            }

            LaunchedEffect(Unit) {
                withContext(ioContextProvider()) {
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
        withContext(ioContextProvider()) {
            notification.start()
        }
    }

    LaunchedEffect(Unit) {
        withContext(ioContextProvider()) {
            mainViewModel.loadBackgroundImage()
        }

        withContext(ioContextProvider()) {
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
