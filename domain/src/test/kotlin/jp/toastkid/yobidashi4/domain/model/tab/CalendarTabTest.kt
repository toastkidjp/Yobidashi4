package jp.toastkid.yobidashi4.domain.model.tab

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
}