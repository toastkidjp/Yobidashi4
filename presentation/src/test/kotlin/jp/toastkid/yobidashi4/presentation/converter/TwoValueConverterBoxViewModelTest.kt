package jp.toastkid.yobidashi4.presentation.converter

import androidx.compose.ui.text.input.TextFieldValue
import jp.toastkid.yobidashi4.domain.service.converter.TsuboCountConverterService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TwoValueConverterBoxViewModelTest {

    private lateinit var subject: TwoValueConverterBoxViewModel

    @BeforeEach
    fun setUp() {
        subject = TwoValueConverterBoxViewModel(TsuboCountConverterService())
    }

    @Test
    fun onFirstValueChange() {
        subject.onFirstValueChange(TextFieldValue("10"))

        assertEquals("10", subject.firstInput().text)
        assertEquals("32.40", subject.secondInput().text)
    }

    @Test
    fun onSecondValueChange() {
        subject.onSecondValueChange(TextFieldValue("3240.0"))

        assertEquals("1000.00", subject.firstInput().text)
        assertEquals("3240.0", subject.secondInput().text)
    }

    @Test
    fun clearFirstInput() {
        subject.clearFirstInput()

        assertTrue(subject.firstInput().text.isEmpty())
    }

    @Test
    fun clearSecondInput() {
        subject.clearSecondInput()

        assertTrue(subject.secondInput().text.isEmpty())
    }

}