package jp.toastkid.yobidashi4.presentation.main.content.data

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.io.path.name

class FileListItemFactoryTest {

    private lateinit var subject: FileListItemFactory

    @MockK
    private lateinit var metaExtractor: FileListItemMetaExtractor

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { metaExtractor.make(any()) } returns FileListItemMeta("test", 0L)

        subject = FileListItemFactory(metaExtractor)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val path = mockk<Path>()
        every { path.name } returns "test.exe"
        val fileListItem1 = subject.invoke(path, true)
        assertTrue(fileListItem1.selected)
        assertFalse(fileListItem1.editable)

        val path2 = mockk<Path>()
        every { path2.name } returns "test2.md"
        val fileListItem2 = subject.invoke(path2)
        assertFalse(fileListItem2.selected)
        assertTrue(fileListItem2.editable)

        verify(exactly = 2) { metaExtractor.make(any()) }
    }

    @Test
    fun metaIsNotFoundCase() {
        every { metaExtractor.make(any()) } returns null
        subject = FileListItemFactory(metaExtractor)

        val path = mockk<Path>()
        every { path.name } returns "test.exe"
        val item = subject.invoke(path, true)

        assertNull(item.subText())
        assertEquals(0L, item.sortKey())
    }

}