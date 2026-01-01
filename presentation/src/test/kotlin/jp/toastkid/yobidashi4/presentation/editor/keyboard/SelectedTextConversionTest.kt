package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.foundation.text.input.TextFieldState
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SelectedTextConversionTest {

    private lateinit var subject: SelectedTextConversion

    @MockK
    private lateinit var conversion: (String) -> String?

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { conversion.invoke(any()) } returns "test"

        subject = SelectedTextConversion()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun emptySelection() {
        val invoke = subject.invoke(
            TextFieldState(),
            0,
            0,
            conversion,
        )

        assertFalse(invoke)
        verify { conversion wasNot Called }
    }

    @Test
    fun conversionFailure() {
        every { conversion.invoke(any()) } returns null
        val content = TextFieldState("Test is good.")

        val invoke = subject.invoke(
            content,
            0,
            4,
            conversion,
        )

        assertFalse(invoke)
        verify { conversion.invoke(any()) }
        assertEquals("Test is good.", content.text)
        assertEquals(13, content.selection.start)
        assertEquals(13, content.selection.end)
    }

    @Test
    fun invoke() {
        val content = TextFieldState("Test is good.")

        val invoke = subject.invoke(
            content,
            0,
            4,
            conversion,
        )

        assertTrue(invoke)
        verify { conversion.invoke("Test") }
        assertEquals("test is good.", content.text)
        assertEquals(0, content.selection.start)
        assertEquals(4, content.selection.end)
    }

}
