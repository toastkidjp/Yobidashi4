/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.slideshow.viewmodel

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import jp.toastkid.yobidashi4.presentation.slideshow.SlideshowWindowViewModel
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch

class SlideshowViewModelTest {

    private lateinit var viewModel: SlideshowWindowViewModel

    @BeforeEach
    fun setUp() {
        viewModel = SlideshowWindowViewModel()
    }

    @Test
    fun windowState() {
        assertNotNull(viewModel.windowState())
    }

    @Test
    fun toggleFullscreen() {
        assertFalse(viewModel.isFloatingWindow())

        viewModel.toggleFullscreen()

        assertTrue(viewModel.isFloatingWindow())

        viewModel.toggleFullscreen()

        assertFalse(viewModel.isFloatingWindow())
    }

    @Test
    fun closeFullscreen() {
        assertFalse(viewModel.isFloatingWindow())

        viewModel.closeFullscreen()

        assertTrue(viewModel.isFloatingWindow())

        viewModel.closeFullscreen()

        assertTrue(viewModel.isFloatingWindow())
    }

    @Test
    fun onEscapeKeyReleasedOnFullscreen() {
        val onCloseWindow = mockk<() -> Unit>()
        every { onCloseWindow.invoke() } just Runs

        viewModel.onEscapeKeyReleased(onCloseWindow)

        verify { onCloseWindow wasNot called }
    }

    @Test
    fun onEscapeKeyReleasedOnWindowed() {
        val onCloseWindow = mockk<() -> Unit>()
        every { onCloseWindow.invoke() } just Runs

        viewModel.closeFullscreen()
        viewModel.onEscapeKeyReleased(onCloseWindow)

        verify { onCloseWindow.invoke() }
    }

    @Test
    fun windowVisible() {
        assertTrue(viewModel.windowVisible())
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEvent() {
        val countDownLatch = CountDownLatch(1)
        viewModel.setOnCloseWindow { countDownLatch.countDown() }

        assertFalse(viewModel.onKeyEvent(KeyEvent(Key.Eight, KeyEventType.KeyUp)))
        assertFalse(viewModel.onKeyEvent(KeyEvent(Key.Escape, KeyEventType.KeyDown)))
        assertTrue(viewModel.onKeyEvent(KeyEvent(Key.Escape, KeyEventType.KeyUp)))
    }

}