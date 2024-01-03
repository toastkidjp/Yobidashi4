package jp.toastkid.yobidashi4.presentation.calendar

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.unmockkAll
import java.time.DayOfWeek
import java.time.LocalDate
import jp.toastkid.yobidashi4.domain.model.calendar.Week
import jp.toastkid.yobidashi4.presentation.viewmodel.calendar.CalendarViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class CalendarViewKtTest {

    @MockK
    private lateinit var calendarViewModel: CalendarViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { calendarViewModel } bind(CalendarViewModel::class)
                }
            )
        }

        every { calendarViewModel.month() } returns mutableListOf(Week().also { it.add(LocalDate.now()) })
        every { calendarViewModel.dayOfWeeks() } returns listOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )
        every { calendarViewModel.isToday(any()) } returns false
        every { calendarViewModel.openDateArticle(any(), any()) } just Runs
        every { calendarViewModel.plusMonths(any()) } just Runs
        every { calendarViewModel.yearInput() } returns TextFieldValue()
        every { calendarViewModel.setYearInput(any()) } just Runs
        every { calendarViewModel.moveMonth(any()) } just Runs
        every { calendarViewModel.moveToCurrentMonth() } just Runs
        every { calendarViewModel.localDate() } returns LocalDate.now()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun calendarView() {
        runDesktopComposeUiTest {
            setContent {
                CalendarView()
            }
        }
    }
}