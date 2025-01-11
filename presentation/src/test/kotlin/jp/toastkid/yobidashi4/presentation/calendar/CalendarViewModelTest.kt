package jp.toastkid.yobidashi4.presentation.calendar

import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.calendar.UserOffDayService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import java.nio.file.Path
import java.time.LocalDate

class CalendarViewModelTest {

    private lateinit var viewModel: CalendarViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var setting: Setting

    @MockK
    private lateinit var userOffDayService: UserOffDayService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier=null) { setting } bind(Setting::class)
                    single(qualifier=null) { userOffDayService } bind(UserOffDayService::class)
                }
            )
        }
        every { mainViewModel.edit(any(), any()) } just Runs
        val path = mockk<Path>()
        every { setting.articleFolderPath() } returns path
        every { path.resolve(any<String>()) } returns mockk()
        every { userOffDayService.findBy(any()) } returns emptySet()

        viewModel = CalendarViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
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

        Assertions.assertEquals(2022, viewModel.localDate().year)
    }

    @Test
    fun moveMonth() {
        viewModel.moveMonth(1)
        Assertions.assertEquals(1, viewModel.localDate().month.value)
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
    fun setYearInput() {
        viewModel.setYearInput(TextFieldValue("2023"))

        Assertions.assertEquals("2023", viewModel.yearInput().text)
    }

    @Test
    fun setYearInputIrregularCase() {
        val year = viewModel.localDate().year

        viewModel.setYearInput(TextFieldValue("TEST"))

        Assertions.assertEquals(year, viewModel.localDate().year)
    }

    @Test
    fun isToday() {
        Assertions.assertAll(
            { Assertions.assertTrue(viewModel.isToday(LocalDate.now().dayOfMonth)) },
            { Assertions.assertFalse(viewModel.isToday(LocalDate.now().plusDays(1).dayOfMonth)) },
            {
                viewModel.plusMonths(1)
                Assertions.assertFalse(viewModel.isToday(LocalDate.now().dayOfMonth))
            },
            {
                viewModel.moveToCurrentMonth()
                viewModel.setYear(2020)
                Assertions.assertFalse(viewModel.isToday(LocalDate.now().dayOfMonth))
            }
        )
    }

    @Test
    fun openDateArticle() {
        Assertions.assertAll(
            {
                viewModel.openDateArticle(20, true)

                verify { setting.articleFolderPath() }
                verify { mainViewModel.edit(any(), true) }
            },
            {
                viewModel.openDateArticle(20, false)

                verify { setting.articleFolderPath() }
                verify { mainViewModel.edit(any(), false) }
            },
            {
                viewModel.openDateArticle(20)

                verify { setting.articleFolderPath() }
                verify { mainViewModel.edit(any(), false) }
            }
        )
    }

    @Test
    fun noopOpenDateArticle() {
        viewModel.openDateArticle(-1)

        verify(inverse = true) { setting.articleFolderPath() }
        verify(inverse = true) { mainViewModel.edit(any(), any()) }
    }

    @Test
    fun dayOfWeek() {
        assertEquals(7, viewModel.dayOfWeeks().size)
    }

    @Test
    fun month() {
        viewModel.moveMonth(2)

        val month = viewModel.month()

        Assertions.assertEquals(5, month.size)
        Assertions.assertTrue(month.flatMap { it.days() }.any { it.offDay })
    }

    @Test
    fun monthChooserState() {
        Assertions.assertFalse(viewModel.openingMonthChooser())

        viewModel.openMonthChooser()

        Assertions.assertTrue(viewModel.openingMonthChooser())

        viewModel.closeMonthChooser()

        Assertions.assertFalse(viewModel.openingMonthChooser())
    }

    @Test
    fun launch() {
        val localDate = LocalDate.of(2024, 2, 12)

        viewModel.launch(localDate)

        assertEquals(localDate.year, viewModel.localDate().year)
        assertEquals(localDate.dayOfYear, viewModel.localDate().dayOfYear)
        assertEquals("2024", viewModel.yearInput().text)
    }

    @Test
    fun onDispose() {
        every { mainViewModel.updateCalendarTab(any(), any(), any()) } just Runs

        viewModel.onDispose(mockk())

        verify { mainViewModel.updateCalendarTab(any(), any(), any()) }
    }

}