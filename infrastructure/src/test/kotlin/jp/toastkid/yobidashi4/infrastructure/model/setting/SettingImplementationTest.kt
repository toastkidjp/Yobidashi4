package jp.toastkid.yobidashi4.infrastructure.model.setting

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
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
        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        every { Files.newBufferedReader(any()) } returns BufferedReader(InputStreamReader(InputStream.nullInputStream()))

        subject = SettingImplementation()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun darkMode() {
    }

    @Test
    fun setDarkMode() {
    }

    @Test
    fun articleFolder() {
    }

    @Test
    fun articleFolderPath() {
    }

    @Test
    fun setUseInternalEditor() {
    }

    @Test
    fun useInternalEditor() {
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
    }

    @Test
    fun save() {
    }

    @Test
    fun wrapLine() {
    }

    @Test
    fun switchWrapLine() {
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