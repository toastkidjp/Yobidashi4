package jp.toastkid.yobidashi4.presentation.compound.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.service.tool.compound.CompoundInterestCalculatorInput
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CompoundInterestCalculatorViewModelTest {

    private lateinit var subject: CompoundInterestCalculatorViewModel

    @BeforeEach
    fun setUp() {
        subject = CompoundInterestCalculatorViewModel()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun setCapitalInput() {
        assertEquals("0", subject.capitalInput().text)

        subject.setCapitalInput(TextFieldValue("10000"))

        assertEquals("10000", subject.capitalInput().text)
        assertEquals(20, subject.result().itemArrays().size)
    }

    @Test
    fun setInstallmentInput() {
        assertEquals("40000", subject.installmentInput().text)

        subject.setInstallmentInput(TextFieldValue("10000"))

        assertEquals("10000", subject.installmentInput().text)
        assertEquals(20, subject.result().itemArrays().size)
    }

    @Test
    fun setAnnualInterestInput() {
        assertEquals("0.03", subject.annualInterestInput().text)

        subject.setAnnualInterestInput(TextFieldValue("0.1"))

        assertEquals("0.1", subject.annualInterestInput().text)
        assertEquals(20, subject.result().itemArrays().size)
    }

    @Test
    fun setYearInput() {
        assertEquals("20", subject.yearInput().text)

        subject.setYearInput(TextFieldValue("10"))

        assertEquals("10", subject.yearInput().text)
        assertEquals(10, subject.result().itemArrays().size)
    }

    @Test
    fun resultIsNullCase() {
        mockkObject(CompoundInterestCalculatorInput)
        every { CompoundInterestCalculatorInput.from(any(), any(), any(), any()) } returns null

        subject.setYearInput(TextFieldValue("2022"))

        verify { CompoundInterestCalculatorInput.from(any(), any(), any(), any()) }
    }

}