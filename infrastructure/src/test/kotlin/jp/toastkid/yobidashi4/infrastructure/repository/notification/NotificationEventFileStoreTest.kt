package jp.toastkid.yobidashi4.infrastructure.repository.notification

import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class NotificationEventFileStoreTest {

    private lateinit var subject: NotificationEventFileStore

    private lateinit var fakeFileSystem: FakeFileSystem

    private lateinit var path: okio.Path

    @BeforeEach
    fun setUp() {
        fakeFileSystem = FakeFileSystem()
        subject = NotificationEventFileStore(fakeFileSystem)

        path = "user/notification/list.tsv".toPath()
        path.parent?.let {
            fakeFileSystem.createDirectories(it)
        }
        fakeFileSystem.write(path) {
            writeUtf8(
                """
Test	This notification is only attempt to send.	2023-12-08 10:51:00
Test2	This notification is only attempt to send.	2023-12-08 10:52:00
Test3	This notification is only attempt to send.	2023-12-08 10:49:00
Test3	This notification is only attempt to send.	2023-13-48 34:49:00
Test3	This notification is only attempt to send.	
Test3	This notification is only attempt to send.
Test3
            
        """
            )
        }
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun add() {
        subject.add(NotificationEvent("test", "test", LocalDateTime.now()))

        fakeFileSystem.source(path).buffer().use {
            assertTrue(it.readUtf8().contains("test\ttest\t"))
        }
    }

    @Test
    fun addIfTheFileDoesNotExists() {
        fakeFileSystem.delete(path)

        subject.add(NotificationEvent("test", "test", LocalDateTime.now()))

        fakeFileSystem.source(path).buffer().use {
            val readUtf8 = it.readUtf8()
            assertTrue(readUtf8.contains("test\ttest\t"))
        }
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
        fakeFileSystem.delete(path)

        assertTrue(subject.readAll().isEmpty())
    }

    @Test
    fun update() {
        subject.update(
            1,
            NotificationEvent(
                "Updated",
                "This notification-event has updated.",
                LocalDateTime.of(2024, 1, 1, 2,3)
            )
        )

        fakeFileSystem.source(path).buffer().use {
            val split = it.readUtf8().split("\n")
            assertEquals("Updated\tThis notification-event has updated.\t2024-01-01 02:03:00", split[1])
            assertEquals(3, split.size)
        }
    }

    @Test
    fun updateWithMinusIndex() {
        subject.update(-1, mockk())

        fakeFileSystem.source(path).buffer().use {
            val split = it.readUtf8().split("\n")
            assertEquals(10, split.size)
        }
    }

    @Test
    fun updateWithOverIndex() {
        val event = mockk<NotificationEvent>()
        every { event.toTsv() } throws RuntimeException()
        subject.update(3, event)

        verify(inverse = true) { event.toTsv() }
    }

    @Test
    fun deleteAt() {
        subject.deleteAt(1)

        fakeFileSystem.source(path).buffer().use {
            assertFalse(it.readUtf8().contains("Test2"))
        }
    }

    @Test
    fun clear() {
        subject.clear()

        assertFalse(fakeFileSystem.exists(path))
    }
}