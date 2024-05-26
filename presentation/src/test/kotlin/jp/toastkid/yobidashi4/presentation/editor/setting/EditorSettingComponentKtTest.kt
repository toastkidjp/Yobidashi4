package jp.toastkid.yobidashi4.presentation.editor.setting

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.Color
import java.awt.GraphicsEnvironment
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class EditorSettingComponentKtTest {

    @MockK
    private lateinit var setting: Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { setting.editorBackgroundColor() } returns Color.WHITE
        every { setting.editorFontSize() } returns 16
        every { setting.editorFontFamily() } returns "Monospace"
        every { setting.editorForegroundColor() } returns Color.BLACK

        mockkConstructor(EditorSettingViewModel::class)
        every { anyConstructed<EditorSettingViewModel>().isOpenFontFamily() } returns false
        every { anyConstructed<EditorSettingViewModel>().isOpenFontSize() } returns false
        every { anyConstructed<EditorSettingViewModel>().editorFontFamily() } returns "test"
        every { anyConstructed<EditorSettingViewModel>().editorFontSize() } returns 15
        every { anyConstructed<EditorSettingViewModel>().setEditorFontFamily(any()) } just Runs
        every { anyConstructed<EditorSettingViewModel>().closeFontFamily() } just Runs
        every { anyConstructed<EditorSettingViewModel>().setEditorFontSize(any()) } just Runs
        every { anyConstructed<EditorSettingViewModel>().closeFontSize() } just Runs
        every { anyConstructed<EditorSettingViewModel>().reset() } just Runs

        startKoin {
            modules(
                module {
                    single(qualifier = null) { setting } bind (Setting::class)
                }
            )
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun editorSettingComponent() {
        runDesktopComposeUiTest {
            setContent {
                EditorSettingComponent(Modifier)
            }

            onNode(hasText("Reset color setting"), true).performClick()
            verify { anyConstructed<EditorSettingViewModel>().reset() }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun fontFamilyDropdown() {
        every { anyConstructed<EditorSettingViewModel>().isOpenFontFamily() } returns true

        runDesktopComposeUiTest {
            setContent {
                EditorSettingComponent(Modifier)
            }

            val fontFamily = GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames[0]
            onNode(hasText(fontFamily), true).assertExists("Not found.").performClick()
            verify { anyConstructed<EditorSettingViewModel>().setEditorFontFamily(any()) }
            verify { anyConstructed<EditorSettingViewModel>().closeFontFamily() }
        }
    }
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun fontSizeDropdown() {
        every { anyConstructed<EditorSettingViewModel>().isOpenFontSize() } returns true

        runDesktopComposeUiTest {
            setContent {
                EditorSettingComponent(Modifier)
            }

            onNode(hasText("24"), true).performClick()
            verify { anyConstructed<EditorSettingViewModel>().setEditorFontSize(any()) }
            verify { anyConstructed<EditorSettingViewModel>().closeFontSize() }
        }
    }

}