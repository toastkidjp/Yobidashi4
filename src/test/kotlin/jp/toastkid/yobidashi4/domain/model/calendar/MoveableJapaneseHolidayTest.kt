package jp.toastkid.yobidashi4.domain.model.calendar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class MoveableJapaneseHolidayTest {

    @Test
    fun testIsTargetMonth() {
        assertTrue(MoveableJapaneseHoliday.isTargetMonth(1))
        assertFalse(MoveableJapaneseHoliday.isTargetMonth(2))
    }

    @Test
    fun testFind() {
        assertNull(MoveableJapaneseHoliday.find(3))
        assertEquals(MoveableJapaneseHoliday.RESPECT_FOR_THE_AGED_DAY, MoveableJapaneseHoliday.find(9))
    }

}