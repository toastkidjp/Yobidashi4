package jp.toastkid.yobidashi4.infrastructure.service.io.file

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.spyk
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.presentation.main.content.data.FileListItemMetaExtractor
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FileListItemMetaExtractorImplementationTest {

    private lateinit var subject: FileListItemMetaExtractor

    private lateinit var fakeFileSystem: FakeFileSystem

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        fakeFileSystem = FakeFileSystem()

        subject = FileListItemMetaExtractorImplementation(fakeFileSystem)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun subTextIsNull() {
        val fileListItem = subject.make("test".toPath().toNioPath())

        assertNull(fileListItem?.subText)
    }

    @Test
    fun subText() {
        val path = "test".toPath()
        fakeFileSystem.write(path) {}
        fakeFileSystem = spyk(fakeFileSystem)
        every { fakeFileSystem.metadata(any()).size } returns 1234567L
        every { fakeFileSystem.metadata(any()).lastModifiedAtMillis } returns 1702169756151
        subject = FileListItemMetaExtractorImplementation(fakeFileSystem)
        val fileListItem = subject.make(path.toNioPath())

        val subText = fileListItem?.subText

        assertEquals("1.18 MB | 2023-12-10(Sun) 09:55:56", subText)
    }

    @Test
    fun subTextUnder1M() {
        val path = "test".toPath()
        fakeFileSystem.write(path) {}
        fakeFileSystem = spyk(fakeFileSystem)
        every { fakeFileSystem.metadata(any()).size } returns 123456L
        every { fakeFileSystem.metadata(any()).lastModifiedAtMillis } returns 1702169756151
        subject = FileListItemMetaExtractorImplementation(fakeFileSystem)

        val fileListItem = subject.make(path.toNioPath())

        assertEquals("120.56 KB | 2023-12-10(Sun) 09:55:56", fileListItem?.subText)
    }

}