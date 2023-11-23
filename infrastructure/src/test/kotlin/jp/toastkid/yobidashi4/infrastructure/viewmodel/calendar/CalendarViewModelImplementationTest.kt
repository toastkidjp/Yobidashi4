package jp.toastkid.yobidashi4.infrastructure.viewmodel.calendar

import androidx.compose.ui.text.input.TextFieldValue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CalendarViewModelImplementationTest {

    private lateinit var viewModel: CalendarViewModelImplementation

    @BeforeEach
    fun setUp() {
        viewModel = CalendarViewModelImplementation()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun localDate() {
        assertNotNull(viewModel.localDate())
    }

    @Test
    fun plusMonths() {
        val month = viewModel.localDate().month
        viewModel.plusMonths(1)
        assertEquals(month + 1, viewModel.localDate().month)
    }

    @Test
    fun plusMonths12() {
        val year = viewModel.localDate().year
        val month = viewModel.localDate().month

        viewModel.plusMonths(12)

        assertEquals(month, viewModel.localDate().month)
        assertEquals(year + 1, viewModel.localDate().year)
    }

    @Test
    fun setYear() {
        viewModel.setYear(2022)

        assertEquals(2022, viewModel.localDate().year)
    }

    @Test
    fun moveMonth() {
        viewModel.moveMonth(1)
        assertEquals(1, viewModel.localDate().month.value)
    }

    @Test
    fun moveToCurrentMonth() {
        val localDate = viewModel.localDate().month
        viewModel.moveMonth(1)

        viewModel.moveToCurrentMonth()

        assertEquals(localDate, viewModel.localDate().month)
    }

    @Test
    fun moveToCurrentMonthWhichContainsMoveYear() {
        viewModel.moveMonth(1)
        viewModel.plusMonths(-1)
        val localDate = viewModel.localDate().year

        viewModel.moveToCurrentMonth()

        assertEquals(localDate + 1, viewModel.localDate().year)
    }

    @Test
    fun getFirstDay() {
        assertEquals(1, viewModel.getFirstDay().dayOfMonth)
    }

    @Test
    fun yearInput() {
        assertEquals("2023", viewModel.yearInput().text)
    }

    @Test
    fun setYearInput() {
        viewModel.setYearInput(TextFieldValue("2023"))

        assertEquals("2023", viewModel.yearInput().text)
    }
}