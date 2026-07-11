package jp.toastkid.yobidashi4.presentation.main.content.data

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
        val fileListItem1 = subject.invoke(mockk(), true, false)
        assertTrue(fileListItem1.selected)
        assertFalse(fileListItem1.editable)

        val fileListItem2 = subject.invoke(mockk())
        assertFalse(fileListItem2.selected)
        assertTrue(fileListItem2.editable)

        verify(exactly = 2) { metaExtractor.make(any()) }
    }

}