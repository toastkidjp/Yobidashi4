package jp.toastkid.yobidashi4.domain.model.article

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import kotlin.io.path.nameWithoutExtension

internal class ArticleTest {

    @MockK
    private lateinit var path: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { path.fileName }.returns(path)
        every { path.nameWithoutExtension }.returns("test")
        mockkStatic(Files::class)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testGetTitle() {
        val article = Article(path)
        assertEquals("test", article.getTitle())
    }

    @Test
    fun testCount() {
        every { Files.readAllLines(any()) }.returns(listOf("test content", "is containing dummy text です"))

        val article = Article(path)
        assertEquals(39, article.count())
    }

    @Test
    fun testMakeFile() {
        every { Files.createFile(any()) }.returns(path)
        every { Files.write(any(), any<ByteArray>()) }.returns(path)

        Article(path).makeFile { "test" }

        verify { Files.createFile(any()) }
        verify { Files.write(any(), any<ByteArray>()) }
    }

    @Test
    fun testPath() {
        assertSame(path, Article(path).path())
    }

    @Test
    fun testLastModified() {
        val fileTime = mockk<FileTime>()
        every { fileTime.toMillis() }.returns(42L)
        every { Files.getLastModifiedTime(any()) }.returns(fileTime)

        val article = Article(path)
        assertEquals(42L, article.lastModified())
    }

    @Test
    fun testLastModifiedThrowingIoException() {
        val fileTime = mockk<FileTime>()
        every { fileTime.toMillis() }.throws(IOException())
        every { Files.getLastModifiedTime(any()) }.returns(fileTime)

        val article = Article(path)
        assertEquals(0L, article.lastModified())
    }

    @Test
    fun testLastModifiedThrowingIoExceptionFromFiles() {
        val fileTime = mockk<FileTime>()
        every { fileTime.toMillis() }.returns(42L)
        every { Files.getLastModifiedTime(any()) }.throws(IOException())

        val article = Article(path)
        assertEquals(0L, article.lastModified())
    }

}