package jp.toastkid.yobidashi4.infrastructure.model.setting

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SettingImplementationTest {

    private lateinit var subject: SettingImplementation

    @MockK
    private lateinit var path: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Path::class)
        every { Path.of(any<String>()) } returns path
        every { path.parent } returns path
        every { path.exists() } returns true
        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        every { Files.newBufferedReader(any()) } returns BufferedReader(InputStreamReader(InputStream.nullInputStream()))
        every { Files.createDirectory(any()) } returns path
        every { Files.newBufferedWriter(any()) } returns BufferedWriter(StringWriter())

        subject = SettingImplementation()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun darkMode() {
        assertFalse(subject.darkMode())

        subject.setDarkMode(true)

        assertTrue(subject.darkMode())
    }

    @Test
    fun articleFolder() {
        assertTrue(subject.articleFolder().isEmpty())
    }

    @Test
    fun articleFolderPath() {
        assertNotNull(subject.articleFolderPath())
        verify { Path.of(any<String>()) }
    }

    @Test
    fun userOffDay() {
    }

    @Test
    fun setUseCaseSensitiveInFinder() {
    }

    @Test
    fun useCaseSensitiveInFinder() {
    }

    @Test
    fun setEditorBackgroundColor() {
    }

    @Test
    fun editorBackgroundColor() {
    }

    @Test
    fun setEditorForegroundColor() {
    }

    @Test
    fun editorForegroundColor() {
    }

    @Test
    fun resetEditorColorSetting() {
    }

    @Test
    fun setEditorFontFamily() {
    }

    @Test
    fun editorFontFamily() {
    }

    @Test
    fun setEditorFontSize() {
    }

    @Test
    fun editorFontSize() {
    }

    @Test
    fun mediaPlayerPath() {
    }

    @Test
    fun mediaFolderPath() {
        assertNull(subject.mediaFolderPath())
    }

    @Test
    fun save() {
        subject.save()

        verify(inverse = true) { Files.createDirectory(any()) }
    }

    @Test
    fun saveWithFolderCreation() {
        every { path.exists() } returns false

        subject.save()

        verify { Files.createDirectory(any()) }
    }

    @Test
    fun switchWrapLine() {
        assertFalse(subject.wrapLine())

        subject.switchWrapLine()

        assertTrue(subject.wrapLine())
    }

    @Test
    fun setMaskingCount() {
        assertEquals(20, subject.getMaskingCount())

        subject.setMaskingCount(3)

        assertEquals(3, subject.getMaskingCount())
    }

    @Test
    fun setUserAgentName() {
        assertTrue(subject.userAgentName().isEmpty())

        subject.setUserAgentName("test")

        assertEquals("test", subject.userAgentName())
    }
}