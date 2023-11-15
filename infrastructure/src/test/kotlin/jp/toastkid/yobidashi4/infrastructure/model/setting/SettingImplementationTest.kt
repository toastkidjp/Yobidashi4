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
    fun userOffDayInitial() {
        assertTrue(subject.userOffDay().isEmpty())
    }

    @Test
    fun userOffDay() {
        every { Files.newBufferedReader(any()) } returns BufferedReader(InputStreamReader("""
user_off_day=12/29,12/30
        """.trimIndent().byteInputStream()))
        subject = SettingImplementation()

        val userOffDay = subject.userOffDay()
        assertEquals(2, userOffDay.size)
        assertEquals(12, userOffDay.get(0).first)
        assertEquals(29, userOffDay.get(0).second)
        assertEquals(12, userOffDay.get(1).first)
        assertEquals(30, userOffDay.get(1).second)
    }

    @Test
    fun setUseCaseSensitiveInFinder() {
        assertFalse(subject.useCaseSensitiveInFinder())

        subject.setUseCaseSensitiveInFinder(true)

        assertTrue(subject.useCaseSensitiveInFinder())
    }

    @Test
    fun setEditorBackgroundColor() {
        assertEquals(java.awt.Color(225, 225, 225, 255),  subject.editorBackgroundColor())

        subject.setEditorBackgroundColor(java.awt.Color.BLACK)
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
    fun editorFontFamily() {
        assertNull(subject.editorFontFamily())

        subject.setEditorFontFamily("test")

        assertEquals("test", subject.editorFontFamily())
    }

    @Test
    fun editorFontSize() {
        assertEquals(14, subject.editorFontSize())

        subject.setEditorFontSize(1)

        assertEquals(1, subject.editorFontSize())

        subject.setEditorFontSize(null)

        assertEquals(1, subject.editorFontSize())
    }

    @Test
    fun mediaPlayerPath() {
        assertNull(subject.mediaPlayerPath())
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