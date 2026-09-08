package jp.toastkid.yobidashi4.presentation.calendar

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.calendar.Week
import jp.toastkid.yobidashi4.domain.model.tab.CalendarTab
import jp.toastkid.yobidashi4.domain.service.calendar.UserOffDayService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate

class CalendarViewKtTest {
    
    @RelaxedMockK
    private lateinit var viewModel: CalendarViewModel
    
    @MockK
    private lateinit var userOffDayService: UserOffDayService

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { userOffDayService.findBy(any()) } returns emptySet()
        every { mainViewModel.updateCalendarTab(any(), any(), any()) } just Runs

        every { viewModel.month() } returns mutableListOf(Week().also { it.add(LocalDate.now()) })
        every { viewModel.dayOfWeeks() } returns listOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )
        every { viewModel.isToday(any()) } returns false
        every { viewModel.openDateArticle(any()) } just Runs
        every { viewModel.openDateArticle(any(), any()) } just Runs
        every { viewModel.plusMonths(any()) } just Runs
        every { viewModel.yearInput() } returns TextFieldState()
        every { viewModel.setYearInput() } just Runs
        every { viewModel.moveMonth(any()) } just Runs
        every { viewModel.closeMonthChooser() } just Runs
        every { viewModel.moveToCurrentMonth() } just Runs
        every { viewModel.launch(any()) } just Runs
        every { viewModel.localDate() } returns LocalDate.now()
        every { viewModel.openingMonthChooser() } returns false
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun calendarView() {
        val date = LocalDate.of(2024, 5, 5)
        every { viewModel.month() } returns mutableListOf(Week().also { it.add(date) })
        every { viewModel.localDate() } returns date

        runDesktopComposeUiTest {
            setContent {
                CalendarView(CalendarTab(), viewModel)
            }

            verify { viewModel.launch(any()) }

            onNodeWithContentDescription("day_label_5", useUnmergedTree = true)
                .assertExists("Not found!")
                .performMouseInput {
                    click()
                    longClick()
                }
            verify { viewModel.openDateArticle(any(), false) }
            verify { viewModel.openDateArticle(any(), true) }

            onNode(hasText("<"), useUnmergedTree = true).onParent().performClick()
                .performKeyInput {
                    pressKey(Key.DirectionLeft, 100L)
                }
            verify { viewModel.plusMonths(-1) }

            onNode(hasText(">"), useUnmergedTree = true).onParent().performClick()
                .performKeyInput {
                    pressKey(Key.DirectionRight, 100L)
                }
            verify { viewModel.plusMonths(1) }

            onNode(hasText("Current month"), useUnmergedTree = true).onParent().performClick()
            verify { viewModel.moveToCurrentMonth() }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun withChooser() {
        val date = LocalDate.of(2024, 5, 5)
        every { viewModel.month() } returns mutableListOf(Week().also { it.add(date) })
        every { viewModel.openingMonthChooser() } returns true

        runDesktopComposeUiTest {
            setContent {
                CalendarView(CalendarTab(), viewModel)
            }

            onNodeWithContentDescription("month_chooser_button_7", useUnmergedTree = true).performClick()
            verify { viewModel.moveMonth(any()) }
            verify { viewModel.closeMonthChooser() }
        }
    }

}