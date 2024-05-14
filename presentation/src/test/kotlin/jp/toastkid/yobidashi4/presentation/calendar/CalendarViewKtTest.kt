package jp.toastkid.yobidashi4.presentation.calendar

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import java.time.DayOfWeek
import java.time.LocalDate
import jp.toastkid.yobidashi4.domain.model.calendar.Week
import jp.toastkid.yobidashi4.domain.model.tab.CalendarTab
import jp.toastkid.yobidashi4.domain.service.calendar.UserOffDayService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class CalendarViewKtTest {
    
    @MockK
    private lateinit var userOffDayService: UserOffDayService

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { userOffDayService } bind(UserOffDayService::class)
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                }
            )
        }
        every { userOffDayService.findBy(any()) } returns emptySet()
        every { mainViewModel.updateCalendarTab(any(), any(), any()) } just Runs

        mockkConstructor(CalendarViewModel::class)
        every { anyConstructed<CalendarViewModel>().month() } returns mutableListOf(Week().also { it.add(LocalDate.now()) })
        every { anyConstructed<CalendarViewModel>().dayOfWeeks() } returns listOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )
        every { anyConstructed<CalendarViewModel>().isToday(any()) } returns false
        every { anyConstructed<CalendarViewModel>().openDateArticle(any()) } just Runs
        every { anyConstructed<CalendarViewModel>().openDateArticle(any(), any()) } just Runs
        every { anyConstructed<CalendarViewModel>().plusMonths(any()) } just Runs
        every { anyConstructed<CalendarViewModel>().yearInput() } returns TextFieldValue()
        every { anyConstructed<CalendarViewModel>().setYearInput(any()) } just Runs
        every { anyConstructed<CalendarViewModel>().moveMonth(any()) } just Runs
        every { anyConstructed<CalendarViewModel>().closeMonthChooser() } just Runs
        every { anyConstructed<CalendarViewModel>().moveToCurrentMonth() } just Runs
        every { anyConstructed<CalendarViewModel>().launch(any()) } just Runs
        every { anyConstructed<CalendarViewModel>().localDate() } returns LocalDate.now()
        every { anyConstructed<CalendarViewModel>().openingMonthChooser() } returns false
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun calendarView() {
        val date = LocalDate.of(2024, 5, 5)
        every { anyConstructed<CalendarViewModel>().month() } returns mutableListOf(Week().also { it.add(date) })
        every { anyConstructed<CalendarViewModel>().localDate() } returns date

        runDesktopComposeUiTest {
            setContent {
                CalendarView(CalendarTab())
            }

            verify { anyConstructed<CalendarViewModel>().launch(any()) }

            onNodeWithTag("day_label_5", useUnmergedTree = true)
                .assertExists("Not found!")
                .performMouseInput {
                    click()
                    longClick()
                }
            verify { anyConstructed<CalendarViewModel>().openDateArticle(any(), false) }
            verify { anyConstructed<CalendarViewModel>().openDateArticle(any(), true) }

            onNode(hasText("<"), useUnmergedTree = true).onParent().performClick()
            verify { anyConstructed<CalendarViewModel>().plusMonths(-1) }

            onNode(hasText(">"), useUnmergedTree = true).onParent().performClick()
            verify { anyConstructed<CalendarViewModel>().plusMonths(1) }

            onNode(hasText("Current month"), useUnmergedTree = true).onParent().performClick()
            verify { anyConstructed<CalendarViewModel>().moveToCurrentMonth() }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun withChooser() {
        val date = LocalDate.of(2024, 5, 5)
        every { anyConstructed<CalendarViewModel>().month() } returns mutableListOf(Week().also { it.add(date) })
        every { anyConstructed<CalendarViewModel>().openingMonthChooser() } returns true

        runDesktopComposeUiTest {
            setContent {
                CalendarView(CalendarTab())
            }

            onNodeWithTag("month_chooser_button_7", useUnmergedTree = true).performClick()
            verify { anyConstructed<CalendarViewModel>().moveMonth(any()) }
            verify { anyConstructed<CalendarViewModel>().closeMonthChooser() }
        }
    }

}