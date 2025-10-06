package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import jp.toastkid.yobidashi4.domain.service.slideshow.SlideDeckReader
import jp.toastkid.yobidashi4.presentation.main.theme.AppTheme
import java.nio.file.Path

class SlideshowWindow {

    @Composable
    fun openWindow(
        path: Path,
        onCloseWindow: () -> Unit
    ) {
        AppTheme(darkTheme = false) {
            val deck = SlideDeckReader(path).invoke()
            val viewModel = remember { SlideshowWindowViewModel() }
            Window(
                onCloseRequest = onCloseWindow,
                undecorated = true,
                state = viewModel.windowState(),
                visible = viewModel.windowVisible(),
                title = deck.title,
            ) {
                Slideshow(
                    deck,
                    { viewModel.onEscapeKeyReleased(onCloseWindow) },
                    viewModel::toggleFullscreen,
                    modifier = Modifier
                )
            }
        }
    }

}
