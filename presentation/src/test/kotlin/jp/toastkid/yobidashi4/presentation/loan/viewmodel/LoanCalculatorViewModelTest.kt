package jp.toastkid.yobidashi4.presentation.loan.viewmodel

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.loan.LoanPayment
import jp.toastkid.yobidashi4.domain.service.loan.LoanPaymentExporter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class LoanCalculatorViewModelTest {

    private lateinit var subject: LoanCalculatorViewModel

    @BeforeEach
    fun setUp() {
        subject = LoanCalculatorViewModel()

        mockkConstructor(LoanPaymentExporter::class)
        every { anyConstructed<LoanPaymentExporter>().invoke(any(), any()) } just Runs

        subject.launch()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun result() {
        assertTrue(subject.result().isEmpty())
    }

    @Test
    fun useZeroInsteadOfEmpty() {
        subject.setLoanAmount(TextFieldValue())

        assertEquals("0", subject.loanAmount().text)
    }

    @Test
    fun useZeroInsteadOfBlank() {
        subject.setLoanAmount(TextFieldValue(" "))

        assertEquals("0", subject.loanAmount().text)
    }

    @Test
    fun loanAmount() {
        subject.setLoanAmount(TextFieldValue("20000000"))

        assertEquals("20000000", subject.loanAmount().text)
    }

    @Test
    fun loanTerm() {
        subject.setLoanTerm(TextFieldValue("5"))

        assertEquals("5", subject.loanTerm().text)
    }

    @Test
    fun interestRate() {
        subject.setInterestRate(TextFieldValue("0.5"))

        assertEquals("0.5", subject.interestRate().text)
    }

    @Test
    fun interestRateWithPlainText() {
        subject.setInterestRate(TextFieldValue("az"))

        assertTrue(subject.interestRate().text.isBlank())
    }

    @Test
    fun downPayment() {
        subject.setDownPayment(TextFieldValue("2000000"))

        assertEquals("2000000", subject.downPayment().text)
    }

    @Test
    fun managementFee() {
        subject.setManagementFee(TextFieldValue("30000"))

        assertEquals("30000", subject.managementFee().text)
    }

    @Test
    fun renovationReserves() {
        subject.setRenovationReserves(TextFieldValue("30001"))

        assertEquals("30001", subject.renovationReserves().text)
    }

    @Test
    fun roundToIntSafely() {
        assertEquals("0", subject.roundToIntSafely(Double.NaN))
        assertEquals("2", subject.roundToIntSafely(2.2))
        assertEquals("5", subject.roundToIntSafely(4.5))
        assertEquals("1,120,004", subject.roundToIntSafely(1120004.17))
    }

    @Test
    fun inputChannel() {
        assertNotNull(subject.inputChannel())
    }

    @Test
    fun setEmpty() {
        subject.setRenovationReserves(TextFieldValue())

        assertEquals("0", subject.renovationReserves().text)
    }

    @Test
    fun setIncludingComma() {
        subject.setRenovationReserves(TextFieldValue("10,000"))

        assertEquals("10000", subject.renovationReserves().text)
    }

    @Test
    fun visualTransformation() {
        val visualTransformation = subject.visualTransformation()

        assertNotSame(VisualTransformation.None, visualTransformation)
    }

    @Test
    fun listState() {
        assertEquals(0, subject.listState().firstVisibleItemIndex)
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEvent() {
        subject.setLastPaymentResult(LoanPayment(30_000, listOf()))

        subject.onKeyEvent(KeyEvent(Key.P, KeyEventType.KeyDown, isCtrlPressed = true))

        verify { anyConstructed<LoanPaymentExporter>().invoke(any(), any()) }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventNotP() {
        subject.setLastPaymentResult(LoanPayment(30_000, listOf()))

        subject.onKeyEvent(KeyEvent(Key.Q, KeyEventType.KeyDown, isCtrlPressed = true))

        verify(inverse = true) { anyConstructed<LoanPaymentExporter>().invoke(any(), any()) }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventNotKeyDown() {
        subject.setLastPaymentResult(LoanPayment(30_000, listOf()))

        subject.onKeyEvent(KeyEvent(Key.P, KeyEventType.KeyUp, isCtrlPressed = true))

        verify(inverse = true) { anyConstructed<LoanPaymentExporter>().invoke(any(), any()) }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventWithoutCtrl() {
        subject.setLastPaymentResult(LoanPayment(30_000, listOf()))

        subject.onKeyEvent(KeyEvent(Key.P, KeyEventType.KeyDown, isCtrlPressed = false))

        verify(inverse = true) { anyConstructed<LoanPaymentExporter>().invoke(any(), any()) }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventWithoutLastPaymentResult() {
        subject.onKeyEvent(KeyEvent(Key.P, KeyEventType.KeyDown, isCtrlPressed = true))

        verify(inverse = true) { anyConstructed<LoanPaymentExporter>().invoke(any(), any()) }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun unConsumedCase() {
        val invoked = subject.onKeyEvent(KeyEvent(Key.LanguageSwitch, KeyEventType.KeyDown))

        assertFalse(invoked)
        verify(inverse = true) { anyConstructed<LoanPaymentExporter>().invoke(any(), any()) }
    }

    @Test
    fun format() {
        assertEquals("100,000", subject.format(100000))
    }

    @CsvSource(
        value = [
            "3000000; 154,000",
            "10000000; 396,000",
            "20000000; 726,000",
            "30000000; 1,056,000",
            "40000000; 1,386,000",
        ],
        delimiter = ';'
    )
    @ParameterizedTest
    fun brokerageFee(amount: String, expected: String) {
        subject.setLoanAmount(TextFieldValue(amount))
        assertEquals(expected, subject.brokerageFee().split(" ")[2])
    }

}