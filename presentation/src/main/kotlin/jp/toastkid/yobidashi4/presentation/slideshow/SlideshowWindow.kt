package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.window.Window
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.service.slideshow.SlideDeckReader
import jp.toastkid.yobidashi4.presentation.main.theme.AppTheme
import jp.toastkid.yobidashi4.presentation.slideshow.viewmodel.SlideshowViewModel

class SlideshowWindow {

    @Composable
    fun openWindow(
        path: Path,
        onCloseWindow: () -> Unit
    ) {
        AppTheme(darkTheme = false) {
            val deck = SlideDeckReader(path).invoke()
            val viewModel = remember { SlideshowViewModel() }
            Window(
                onCloseRequest = onCloseWindow,
                undecorated = true,
                state = viewModel.windowState(),
                visible = viewModel.windowVisible(),
                title = deck.title,
            ) {
                val focusRequester = remember { FocusRequester() }

                Slideshow(
                    deck,
                    { viewModel.onEscapeKeyReleased(onCloseWindow) },
                    { viewModel.toggleFullscreen() },
                    modifier = Modifier.focusRequester(focusRequester)
                )

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        }
    }

}
