package jp.toastkid.yobidashi4.domain.model.tab

import java.time.Month
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CalendarTabTest {

    private lateinit var calendarTab: CalendarTab

    @BeforeEach
    fun setUp() {
        calendarTab = CalendarTab()
    }

    @Test
    fun title() {
        assertEquals("Calendar", calendarTab.title())
    }

    @Test
    fun closeable() {
        assertTrue(calendarTab.closeable())
    }

    @Test
    fun iconPath() {
        assertTrue(calendarTab.iconPath().startsWith("images/icon/"))
    }

    @Test
    fun localDate() {
        val tab = CalendarTab(2024, 2)

        val localDate = tab.localDate()

        assertEquals(2024, localDate.year)
        assertEquals(Month.FEBRUARY, localDate.month)
        assertEquals(1, localDate.dayOfMonth)
    }

}