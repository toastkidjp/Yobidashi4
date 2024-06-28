package jp.toastkid.yobidashi4.domain.model.notification

import java.time.LocalDateTime
import java.time.Month
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.fail

class NotificationEventTest {

    private lateinit var subject: NotificationEvent

    @BeforeEach
    fun setUp() {
        subject = NotificationEvent("test", "message", LocalDateTime.of(2023, 12, 23, 12, 33, 44))
    }

    @Test
    fun dateTimeString() {
        println(subject.dateTimeString())
    }

    @Test
    fun toTsv() {
        println(subject.toTsv())
    }

    @Test
    fun parse() {
        val dateTime = NotificationEvent.parse("2022-12-21 12:33:44") ?: fail("")
        assertAll(
            { assertEquals(2022, dateTime.year) },
            { assertEquals(Month.DECEMBER, dateTime.month) },
            { assertEquals(21, dateTime.dayOfMonth) },
            { assertEquals(12, dateTime.hour) },
            { assertEquals(33, dateTime.minute) },
            { assertEquals(44, dateTime.second) },
        )
    }

    @Test
    fun parseFailure() {
        assertNull(NotificationEvent.parse("2022-12-32 12:33:44"))
        assertNull(NotificationEvent.parse("2022-12-30 2:33:44"))
        assertNull(NotificationEvent.parse("test"))
        assertNull(NotificationEvent.parse(""))
    }

    @Test
    fun makeDefault() {
        val (title, text, date) = NotificationEvent.makeDefault()
        assertEquals("New", title)
        assertEquals("New notification's message", text)
    }

}