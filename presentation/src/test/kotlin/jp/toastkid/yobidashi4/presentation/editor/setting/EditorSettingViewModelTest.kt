package jp.toastkid.yobidashi4.presentation.editor.setting

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import java.awt.Color

class EditorSettingViewModelTest {

    private lateinit var subject: EditorSettingViewModel

    @MockK
    private lateinit var setting: Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { setting.editorBackgroundColor() } returns Color.BLACK
        every { setting.editorForegroundColor() } returns Color.WHITE
        every { setting.setEditorBackgroundColor(any()) } just Runs
        every { setting.setEditorForegroundColor(any()) } just Runs
        every { setting.setEditorFontFamily(any()) } just Runs
        every { setting.setEditorFontSize(any()) } just Runs
        every { setting.resetEditorColorSetting() } just Runs
        every { setting.editorFontFamily() } returns "Monospace"
        every { setting.editorFontSize() } returns 14

        startKoin {
            modules(
                module {
                    single(qualifier = null) { setting } bind(Setting::class)
                }
            )
        }

        subject = EditorSettingViewModel()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun editorFontFamily() {
        assertEquals("Monospace", subject.editorFontFamily())
    }

    @Test
    fun setEditorFontFamily() {
        subject.setEditorFontFamily("Hiragino")

        verify { setting.setEditorFontFamily("Hiragino") }
    }

    @Test
    fun editorFontSize() {
        assertEquals(14, subject.editorFontSize())
    }

    @Test
    fun setEditorFontSize() {
        subject.setEditorFontSize(12)

        verify { setting.setEditorFontSize(12) }
    }

    @Test
    fun setEditorFontSizePassingOtherClass() {
        subject.setEditorFontSize("12")

        verify(inverse = true) { setting.setEditorFontSize(any()) }
    }

    @Test
    fun openFontFamily() {
        assertFalse(subject.isOpenFontFamily())

        subject.openFontFamily()

        assertTrue(subject.isOpenFontFamily())

        subject.closeFontFamily()

        assertFalse(subject.isOpenFontFamily())
    }

    @Test
    fun openFontSize() {
        assertFalse(subject.isOpenFontSize())

        subject.openFontSize()

        assertTrue(subject.isOpenFontSize())

        subject.closeFontSize()

        assertFalse(subject.isOpenFontSize())
    }

    @Test
    fun reset() {
        subject.reset()

        verify { setting.resetEditorColorSetting() }
    }

}