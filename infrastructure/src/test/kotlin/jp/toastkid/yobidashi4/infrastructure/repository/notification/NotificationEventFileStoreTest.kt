package jp.toastkid.yobidashi4.infrastructure.repository.notification

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NotificationEventFileStoreTest {

    private lateinit var subject: NotificationEventFileStore

    @BeforeEach
    fun setUp() {
        subject = NotificationEventFileStore()

        mockkStatic(Path::class, Files::class)
        every { Path.of(any<String>()) } returns mockk()
        every { Files.write(any(), any<Iterable<String>>()) } returns mockk()
        every { Files.write(any(), any<ByteArray>()) } returns mockk()
        every { Files.write(any(), any<ByteArray>(), StandardOpenOption.APPEND) } returns mockk()
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
    fun add() {
        subject.add(NotificationEvent("test", "test", LocalDateTime.now()))

        verify { Files.write(any(), any<ByteArray>(), StandardOpenOption.APPEND) }
    }

    @Test
    fun readAll() {
        val events = subject.readAll()
        assertEquals(3, events.size)
        events.forEach {
            assertTrue(it.title.startsWith("Test"))
            assertTrue(LocalDateTime.now().isAfter(it.date))
        }
    }

    @Test
    fun fileDoseNotExistsCase() {
        every { Files.exists(any()) } returns false

        assertTrue(subject.readAll().isEmpty())
    }

    @Test
    fun update() {
        val slot = slot<Iterable<String>>()
        every { Files.write(any(), capture(slot)) } returns mockk()

        subject.update(1, NotificationEvent("Updated", "This notification-event has updated.", LocalDateTime.of(2024, 1, 1, 2,3)))

        verify { Files.write(any(), any<Iterable<String>>()) }
        val toList = slot.captured.toList()
        assertEquals("Updated\tThis notification-event has updated.\t2024-01-01 02:03:00", toList[1])
        assertEquals(3, toList.size)
    }

    @Test
    fun updateWithMinusIndex() {
        subject.update(-1, mockk())

        verify(inverse = true) { Files.write(any(), any<Iterable<String>>()) }
    }

    @Test
    fun updateWithOverIndex() {
        subject.update(3, mockk())

        verify(inverse = true) { Files.write(any(), any<Iterable<String>>()) }
    }

    @Test
    fun deleteAt() {
        subject.deleteAt(1)

        verify { Files.write(any(), any<Iterable<String>>()) }
    }

    @Test
    fun clear() {
        subject.clear()

        verify { Files.write(any(), any<ByteArray>()) }
    }
}