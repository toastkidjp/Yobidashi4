package jp.toastkid.yobidashi4.presentation.slideshow.viewmodel

import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState

class SlideshowViewModel {

    private val windowState = WindowState(placement = WindowPlacement.Maximized)

    fun windowState() = windowState

    fun toggleFullscreen() {
        if (windowState.placement == WindowPlacement.Maximized) {
            windowState.placement = WindowPlacement.Floating
        } else {
            windowState.placement = WindowPlacement.Maximized
        }
    }

    fun closeFullscreen() {
        windowState.placement = WindowPlacement.Floating
    }

}