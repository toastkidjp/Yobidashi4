package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SelectedTextConversionTest {

    private lateinit var subject: SelectedTextConversion

    @MockK
    private lateinit var conversion: (String) -> String?

    @MockK
    private lateinit var setNewContent: (TextFieldValue) -> Unit

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { conversion.invoke(any()) } returns "test"
        every { setNewContent.invoke(any()) } just Runs

        subject = SelectedTextConversion()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun emptySelection() {
        val invoke = subject.invoke(
            TextFieldValue(),
            0,
            0,
            conversion,
            setNewContent
        )

        assertFalse(invoke)
        verify { conversion wasNot Called }
        verify { setNewContent wasNot Called }
    }

    @Test
    fun conversionFailure() {
        every { conversion.invoke(any()) } returns null

        val invoke = subject.invoke(
            TextFieldValue("Test is good."),
            0,
            4,
            conversion,
            setNewContent
        )

        assertFalse(invoke)
        verify { conversion.invoke(any()) }
        verify { setNewContent wasNot Called }
    }

    @Test
    fun invoke() {
        val invoke = subject.invoke(
            TextFieldValue("Test is good."),
            0,
            4,
            conversion,
            setNewContent
        )

        assertTrue(invoke)
        verify { conversion.invoke("Test") }
        verify { setNewContent.invoke(any()) }
    }

}
