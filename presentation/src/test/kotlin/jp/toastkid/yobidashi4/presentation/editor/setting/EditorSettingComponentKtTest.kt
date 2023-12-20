package jp.toastkid.yobidashi4.presentation.editor.setting

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import java.awt.Color
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
        }
    }

}