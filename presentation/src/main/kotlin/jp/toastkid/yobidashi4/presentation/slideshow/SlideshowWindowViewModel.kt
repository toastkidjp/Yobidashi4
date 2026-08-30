package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import java.util.concurrent.atomic.AtomicReference

class SlideshowWindowViewModel {

    private val windowState = WindowState(placement = WindowPlacement.Maximized)

    fun windowState() = windowState

    fun toggleFullscreen() {
        windowState.placement =
            if (windowState.placement == WindowPlacement.Maximized) WindowPlacement.Floating
            else WindowPlacement.Maximized
    }

    fun closeFullscreen() {
        windowState.placement = WindowPlacement.Floating
    }

    fun isFloatingWindow() = windowState.placement == WindowPlacement.Floating

    fun onEscapeKeyReleased(onCloseWindow: () -> Unit) {
        if (isFloatingWindow()) {
            onCloseWindow()
            return
        }

        closeFullscreen()
    }

    fun windowVisible() = true

    private val onCloseWindow : AtomicReference<() -> Unit> = AtomicReference()

    fun setOnCloseWindow(action: () -> Unit) {
        onCloseWindow.set(action)
    }

    fun onKeyEvent(keyEvent: KeyEvent): Boolean {
        if (keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.Escape) {
            onCloseWindow.get().invoke()
            return true
        }
        return false
    }

}