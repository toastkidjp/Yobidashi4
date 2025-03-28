package jp.toastkid.yobidashi4.domain.service.article

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class UserTemplateStreamReaderTest {

    @InjectMockKs
    private lateinit var userTemplateStreamReader: UserTemplateStreamReader

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Path::class)
        every { Path.of(any<String>()) } returns mockk()

        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        every { Files.newInputStream(any()) } returns mockk()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testInvoke() {
        userTemplateStreamReader.invoke()

        verify { Files.exists(any()) }
        verify { Files.newInputStream(any()) }
    }

    @Test
    fun testReadingDefaultTemplateCase() {
        every { Files.exists(any()) } returns false

        val inputStream = userTemplateStreamReader.invoke()

        inputStream?.use {
            assertEquals("# {{title}}\n", String(it.readBytes()))
        }
        verify { Files.exists(any()) }
        verify(inverse = true) { Files.newInputStream(any()) }
    }

}