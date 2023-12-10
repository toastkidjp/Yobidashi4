package jp.toastkid.yobidashi4.domain.service.notification

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.nio.file.Files
import java.time.LocalDateTime
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NotificationEventReaderTest {

    private lateinit var notificationEventReader: NotificationEventReader

    @BeforeEach
    fun setUp() {
        notificationEventReader = NotificationEventReader()

        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        every { Files.readAllLines(any()) } returns """
Test	This notification is only attempt to send.	2023-12-08 10:51:00
Test2	This notification is only attempt to send.	2023-12-08 10:52:00
Test3	This notification is only attempt to send.	2023-12-08 10:49:00
Test3	This notification is only attempt to send.	
Test3	This notification is only attempt to send.
Test3
            
        """.split("\n")
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val events = notificationEventReader.invoke()
        assertEquals(3, events.size)
        events.forEach {
            assertTrue(it.title.startsWith("Test"))
            assertTrue(LocalDateTime.now().isAfter(it.date))
        }
    }

    @Test
    fun fileDoseNotExistsCase() {
        every { Files.exists(any()) } returns false

        assertTrue(notificationEventReader.invoke().isEmpty())
    }

}