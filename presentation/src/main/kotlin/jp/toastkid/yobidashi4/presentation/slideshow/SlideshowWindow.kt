package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.service.slideshow.SlideDeckReader
import jp.toastkid.yobidashi4.main.AppTheme

class SlideshowWindow {

    fun show(path: Path, exitApplicationOnCloseRequest: Boolean = true) {
        application {
            AppTheme {
                val deck = SlideDeckReader(path).invoke()
                Window(
                    onCloseRequest = {
                        if (exitApplicationOnCloseRequest) {
                            exitApplication()
                        }
                    },
                    title = deck.title,
                ) {
                    val focusRequester = remember { FocusRequester() }

                    Slideshow(deck, modifier = Modifier.focusRequester(focusRequester))

                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                }
            }
        }
    }

}

fun main(args: Array<String>) {
    SlideshowWindow().show(Path.of(args[0]))
}