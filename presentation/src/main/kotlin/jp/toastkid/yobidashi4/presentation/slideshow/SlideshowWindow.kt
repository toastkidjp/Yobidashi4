package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import jp.toastkid.yobidashi4.domain.service.slideshow.SlideDeckReader
import jp.toastkid.yobidashi4.presentation.main.theme.AppTheme
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.file.Path

class SlideshowWindow : KoinComponent {

    private val slideDeckReader: SlideDeckReader by inject()

    @Composable
    fun openWindow(
        path: Path,
        onCloseWindow: () -> Unit
    ) {
        val deck = slideDeckReader(path)
        if (deck.slides.isEmpty()) {
            return
        }

        AppTheme(darkTheme = false) {
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
