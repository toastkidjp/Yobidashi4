package jp.toastkid.yobidashi4.presentation.main.content.data

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FileListItemTest {

    @MockK
    private lateinit var path: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Files::class)
        every { Files.exists(path) } returns true
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test() {
        val fileListItem = FileListItem(path, true)
        assertTrue(fileListItem.selected)
        assertFalse(fileListItem.unselect().selected)
        assertFalse(fileListItem.reverseSelection().selected)
    }

    @Test
    fun testEditable() {
        val fileListItem = FileListItem(path, true, true)
        assertTrue(fileListItem.selected)
        assertFalse(fileListItem.unselect().selected)
        assertFalse(fileListItem.reverseSelection().selected)
        assertTrue(fileListItem.editable)
        assertTrue(fileListItem.reverseSelection().editable)
    }

    @Test
    fun subTextIsNull() {
        val fileListItem = FileListItem(path, true, true)
        every { Files.exists(path) } returns false

        assertNull(fileListItem.subText())
    }

    @Test
    fun subText() {
        every { Files.size(any()) } returns 1234567
        every { Files.getLastModifiedTime(any()) } returns FileTime.fromMillis(1702169756151)
        val fileListItem = FileListItem(path, true, true)

        val subText = fileListItem.subText()

        verify { Files.size(any()) }
        verify { Files.getLastModifiedTime(any()) }
        assertEquals("1234 KB | 2023-12-10(Sun) 09:55:56", subText)
    }

}