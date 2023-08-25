package jp.toastkid.yobidashi4.presentation.slideshow.viewmodel

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SlideshowViewModelTest {

    private lateinit var viewModel: SlideshowViewModel

    @BeforeEach
    fun setUp() {
        viewModel = SlideshowViewModel()
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

}