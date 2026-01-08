package jp.toastkid.yobidashi4.presentation.compound.viewmodel

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.service.tool.compound.CompoundInterestCalculatorInput
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

        assertEquals(0, subject.result().size)

        subject.clearCapitalInput()

        assertTrue(subject.capitalInput().text.isEmpty())
    }

    @Test
    fun setInstallmentInput() {
        assertEquals("40000", subject.installmentInput().text)

        assertEquals(0, subject.result().size)

        subject.clearInstallmentInput()

        assertTrue(subject.installmentInput().text.isEmpty())
    }

    @Test
    fun setAnnualInterestInput() {
        assertEquals("0.03", subject.annualInterestInput().text)

        assertEquals(0, subject.result().size)

        subject.clearAnnualInterestInput()

        assertTrue(subject.annualInterestInput().text.isEmpty())
    }

    @Test
    fun setYearInput() {
        assertEquals("20", subject.yearInput().text)

        assertEquals(0, subject.result().size)

        subject.clearYearInput()

        assertTrue(subject.yearInput().text.isEmpty())
    }

    @Test
    fun resultIsNullCase() {
        mockkObject(CompoundInterestCalculatorInput)
        every { CompoundInterestCalculatorInput.from(any(), any(), any(), any()) } returns null

        subject.calculate()

        verify { CompoundInterestCalculatorInput.from(any(), any(), any(), any()) }
    }

}