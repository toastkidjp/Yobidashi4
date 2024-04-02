package jp.toastkid.yobidashi4.domain.model.web.icon

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.pathString
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WebIconTest {

    private lateinit var webIcon: WebIcon

    @MockK
    private lateinit var path: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(Path::class)
        mockkStatic(Files::class)
        every { Path.of(any<String>()) } returns path

        webIcon = WebIcon()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun makeFolderNoopIfNotNeed() {
        every { Files.exists(any()) } returns true
        every { Files.createDirectories(any()) } returns path

        webIcon.makeFolderIfNeed()

        verify { Files.exists(any()) }
        verify(inverse = true) { Files.createDirectories(any()) }
    }

    @Test
    fun makeFolderIfNeed() {
        every { Files.exists(any()) } returns false
        every { Files.createDirectories(any()) } returns path

        webIcon.makeFolderIfNeed()

        verify { Files.exists(any()) }
        verify { Files.createDirectories(any()) }
    }

    @Test
    fun faviconFolder() {
        assertSame(path, webIcon.faviconFolder())
    }

    @Test
    fun find() {
        val path1 = mockk<Path>()
        every { path1.fileName.pathString } returns "www.yahoo.co.jp.png"
        every { Files.list(any()) } returns Stream.of(path1)

        val find = webIcon.find("https://www.yahoo.co.jp")

        assertNotNull(find)
    }

    @Test
    fun notFoundCase() {
        every { Files.list(any()) } returns Stream.empty()

        val find = webIcon.find("https://www.yahoo.co.jp")

        assertNull(find)
    }

    @Test
    fun unknownSchemeCase() {
        val find = webIcon.find("tel:0120112112")

        assertNull(find)
    }

    @Test
    fun readAll() {
        every { Files.list(any()) } returns Stream.of(mockk())

        webIcon.readAll()

        verify { Files.list(any()) }
    }
}