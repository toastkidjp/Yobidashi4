package jp.toastkid.yobidashi4.domain.service.archive

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime

class ZipArchiverTest {

    @InjectMockKs
    private lateinit var zipArchiver: ZipArchiver

    @MockK
    private lateinit var outputFolder: Path

    @MockK
    private lateinit var path: Path

    @MockK
    private lateinit var fileName: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Path::class)
        every { outputFolder.resolve(any<String>()) } returns mockk()
        mockkStatic(Files::class)
        every { Files.newOutputStream(any()) } returns ByteArrayOutputStream()
        every { Files.getLastModifiedTime(any()) } returns FileTime.fromMillis(System.currentTimeMillis())
        every { Files.newInputStream(path) } returns "test".byteInputStream()
        every { Files.exists(any()) } returns true
        every { Files.createDirectories(any()) } returns mockk()

        every { path.fileName } returns fileName
        every { fileName.toString() } returns "file.md"
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        zipArchiver.invoke(listOf(path))

        verify { Files.newOutputStream(any()) }
        verify { path.fileName }
        verify { fileName.toString() }
        verify { Files.exists(any()) }
        verify(inverse = true) { Files.createDirectories(any()) }
    }

    @Test
    fun exceptionCase() {
        val mockk = mockk<IOException>()
        every { mockk.printStackTrace() } just Runs
        every { Files.getLastModifiedTime(any()) } throws mockk

        zipArchiver.invoke(listOf(path))

        verify { Files.getLastModifiedTime(any()) }
        verify { mockk.printStackTrace() }
    }

    @Test
    fun exceptionCase2() {
        val mockk = mockk<IOException>()
        every { mockk.printStackTrace() } just Runs
        every { Files.newOutputStream(any()) } throws mockk

        zipArchiver.invoke(listOf(path), "failureCase.zip")

        verify { Files.newOutputStream(any()) }
        verify { mockk.printStackTrace() }
    }

    @Test
    fun exceptionWithNullZipStreamCase() {
        val mockk = mockk<IOException>()
        every { mockk.printStackTrace() } just Runs
        every { Files.newOutputStream(any()) } throws mockk

        zipArchiver.invoke(listOf(path))

        verify { mockk.printStackTrace() }
    }

    @Test
    fun makeFoldersIfNeeds() {
        every { Files.exists(any()) } returns false

        zipArchiver.invoke(emptyList())

        verify { Files.exists(any()) }
        verify { Files.createDirectories(any()) }
    }

}