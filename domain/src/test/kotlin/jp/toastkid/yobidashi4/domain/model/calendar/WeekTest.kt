package jp.toastkid.yobidashi4.domain.model.calendar

import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class WeekTest {

    private val week: Week = Week()

    @Test
    fun test() {
        week.add(LocalDate.of(2023, 8, 6), false)
        week.add(LocalDate.of(2023, 8, 7), false)
        week.add(LocalDate.of(2023, 8, 8), false)
        week.add(LocalDate.of(2023, 8, 9), false)
        week.add(LocalDate.of(2023, 8, 10), false)
        week.add(LocalDate.of(2023, 8, 11), true)
        week.add(LocalDate.of(2023, 8, 12), false)
        week.addEmpty()

        assertEquals(8, week.days().size)
        assertTrue(week.anyApplicableDate())
    }

}