package jp.toastkid.yobidashi4.domain.service.calendar.us

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AmericanOffDayFinderTest {

    private lateinit var subject: AmericanOffDayFinder

    @BeforeEach
    fun setUp() {
        subject = AmericanOffDayFinder()
    }

    @Test
    fun invoke() {
        assertEquals(2, subject.invoke(2025, 1, false).size)
    }

}