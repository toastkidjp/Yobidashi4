package jp.toastkid.yobidashi4.presentation.main.title

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LauncherJarTimestampReaderTest {

    private lateinit var subject: LauncherJarTimestampReader

    @BeforeEach
    fun setUp() {
        subject = LauncherJarTimestampReader()

        mockkStatic(Path::class, Files::class)
        every { Path.of(any<String>()) } returns mockk()
        every { Files.exists(any()) } returns true
        every { Files.getLastModifiedTime(any()) } returns FileTime.fromMillis(1702169756151)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val lastUpdated = subject.invoke("fantastic.jar")

        assertEquals("2023-12-10(Sun) 09:55:56", lastUpdated)
    }

    @Test
    fun jarFileDoesNotExists() {
        every { Files.exists(any()) } returns false

        val lastUpdated = subject.invoke("fantastic.jar")

        assertNull(lastUpdated)
    }

    @Test
    fun mainCase() {
        val lastUpdated = subject.invoke()

        assertNull(lastUpdated)
    }

}