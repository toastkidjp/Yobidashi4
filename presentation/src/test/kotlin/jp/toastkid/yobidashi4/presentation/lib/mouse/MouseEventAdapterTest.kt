package jp.toastkid.yobidashi4.presentation.lib.mouse

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MouseEventAdapterTest {

    private lateinit var subject: MouseEventAdapter

    @MockK
    private lateinit var event: PointerEvent

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        subject = MouseEventAdapter()
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun isSecondaryClick() {
        val pointerInputChange = mockk<PointerInputChange>()
        every { pointerInputChange.previousPressed } returns false
        every { pointerInputChange.pressed } returns true
        event = spyk(PointerEvent(listOf(pointerInputChange)))
        every { event.button } returns PointerButton.Secondary

        assertTrue(subject.isSecondaryClick(event))
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun isNotSecondaryClick() {
        val pointerInputChange = mockk<PointerInputChange>()
        every { pointerInputChange.previousPressed } returns false
        every { pointerInputChange.pressed } returns true
        event = spyk(PointerEvent(listOf(pointerInputChange)))
        every { event.button } returns PointerButton.Primary

        assertFalse(subject.isSecondaryClick(event))
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun isNotClick() {
        val pointerInputChange = mockk<PointerInputChange>()
        every { pointerInputChange.previousPressed } returns false
        every { pointerInputChange.pressed } returns false
        event = spyk(PointerEvent(listOf(pointerInputChange)))
        every { event.button } returns PointerButton.Secondary

        assertFalse(subject.isSecondaryClick(event))
    }

}