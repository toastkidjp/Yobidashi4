package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DecimalVisualTransformationTest {

    private lateinit var subject: OutputTransformation

    @BeforeEach
    fun setUp() {
        subject = DecimalVisualTransformation()
    }

    @Test
    fun commaInsertion() {
        val buffer = mockk<TextFieldBuffer>()
        every { buffer.asCharSequence() } returns "1000000"
        every { buffer.replace(any(), any(), any()) } just Runs
        with(subject) {
            buffer.transformOutput()
        }
        verify { buffer.replace(1, 1, ",") }
        verify { buffer.replace(5, 5, ",") }
    }

    @Test
    fun noopInsertion() {
        val buffer = mockk<TextFieldBuffer>()
        every { buffer.asCharSequence() } returns "0.33343"
        every { buffer.replace(any(), any(), any()) } just Runs
        with(subject) {
            buffer.transformOutput()
        }
        verify(inverse = true) { buffer.replace(any(), any(), ",") }
    }

    @Test
    fun zero() {
        val buffer = mockk<TextFieldBuffer>()
        every { buffer.asCharSequence() } returns "0"
        every { buffer.replace(any(), any(), any()) } just Runs
        with(subject) {
            buffer.transformOutput()
        }
        verify(inverse = true) { buffer.replace(any(), any(), ",") }
    }

    @Test
    fun irregularCase() {
        val buffer = mockk<TextFieldBuffer>()
        every { buffer.asCharSequence() } returns "test"
        every { buffer.replace(any(), any(), any()) } just Runs
        with(subject) {
            buffer.transformOutput()
        }
        verify(inverse = true) { buffer.replace(any(), any(), ",") }
    }

}