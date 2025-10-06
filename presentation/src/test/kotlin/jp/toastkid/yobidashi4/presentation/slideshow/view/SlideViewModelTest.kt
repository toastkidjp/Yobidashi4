package jp.toastkid.yobidashi4.presentation.slideshow.view

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull

class SlideViewModelTest {

    private lateinit var subject: SlideViewModel

    @BeforeEach
    fun setUp() {
        subject = SlideViewModel()
    }

    @Test
    fun scrollState() {
        assertEquals(0, subject.scrollState().value)
    }

    @Test
    fun focusRequester() {
        assertNotNull(subject.focusRequester())
    }

    @Test
    fun requestFocus() {
    }

}