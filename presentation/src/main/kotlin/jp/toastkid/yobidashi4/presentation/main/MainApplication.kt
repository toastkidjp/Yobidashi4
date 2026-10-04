package jp.toastkid.yobidashi4.presentation.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.LocalTextContextMenu
import androidx.compose.foundation.text.TextContextMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import jp.toastkid.yobidashi4.domain.service.io.IoContextProvider
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.icon
import jp.toastkid.yobidashi4.presentation.lib.annotation.ExcludeCoverageCalculation
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
import org.koin.compose.koinInject
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

@ExcludeCoverageCalculation
@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun ApplicationScope.Application(localTextContextMenu: ProvidableCompositionLocal<TextContextMenu>) {
    Application(
        localTextContextMenu,
        koinInject(),
        koinInject(),
        koinInject()
    )
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun ApplicationScope.Application(
    localTextContextMenu: ProvidableCompositionLocal<TextContextMenu>,
    mainViewModel: MainViewModel,
    viewModel: MainApplicationViewModel,
    ioContextProvider: IoContextProvider
) {
    AppTheme(darkTheme = mainViewModel.darkMode()) {
        MainTray()

        Window(
            onCloseRequest = ::exitApplication,
            title = "Yobidashi 4 ${LauncherJarTimestampReader().invoke() ?: ""}",
            state = mainViewModel.windowState(),
            visible = viewModel.windowVisible(),
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

    LaunchedEffect(Unit) {
        withContext(ioContextProvider()) {
            viewModel.startNotification()
        }
    }

    LaunchedEffect(Unit) {
        withContext(ioContextProvider()) {
            mainViewModel.loadBackgroundImage()
        }
    }

    LaunchedEffect(Unit) {
        withContext(ioContextProvider()) {
            viewModel.startReceiveNotification()
        }
    }

    LaunchedEffect(viewModel.windowVisible()) {
        viewModel.exitApplicationIfNeed(::exitApplication)
    }
}
