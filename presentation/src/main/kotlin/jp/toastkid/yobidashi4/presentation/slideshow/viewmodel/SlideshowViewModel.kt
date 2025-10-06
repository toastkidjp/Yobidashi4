/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.slideshow.viewmodel

import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState

class SlideshowViewModel {

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

}