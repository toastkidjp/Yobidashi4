package jp.toastkid.yobidashi4.domain.service.article

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Month

class JapaneseStockMarketCloseDaysFinderTest {

    private lateinit var subject: JapaneseStockMarketCloseDaysFinder

    @BeforeEach
    fun setUp() {
        subject = JapaneseStockMarketCloseDaysFinder()
    }

    @Test
    fun invoke() {
        assertFalse(subject.invoke(2024, Month.JUNE, 1))
        assertFalse(subject.invoke(2024, Month.MAY, 1))
        assertTrue(subject.invoke(2024, Month.MAY, 3))
        assertFalse(subject.invoke(2024, Month.DECEMBER, 30))
        assertTrue(subject.invoke(2024, Month.DECEMBER, 31))
        assertTrue(subject.invoke(2024, Month.JANUARY, 1))
        assertTrue(subject.invoke(2024, Month.JANUARY, 2))
        assertTrue(subject.invoke(2024, Month.JANUARY, 3))
        assertFalse(subject.invoke(2024, Month.JANUARY, 4))
    }
}