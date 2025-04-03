package jp.toastkid.yobidashi4.presentation.setting

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.withKeyDown
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class SettingEditorViewKtTest {

    @MockK
    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var setting: Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mockkConstructor(SettingEditorViewModel::class)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { viewModel } bind(MainViewModel::class)
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }

        every { anyConstructed<SettingEditorViewModel>().items() } returns listOf(
            "test" to TextFieldValue(),
            "test2" to TextFieldValue(),
            "test3" to TextFieldValue("cursor_target")
        )
        every { anyConstructed<SettingEditorViewModel>().start() } just Runs
        every { anyConstructed<SettingEditorViewModel>().save() } just Runs
        every { anyConstructed<SettingEditorViewModel>().openFile() } just Runs
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun settingEditorView() {
        runComposeUiTest {
            setContent {
                SettingEditorView()
            }

            onNodeWithText("Save").performClick()
            onNodeWithText("Open").performClick()
            onNodeWithText("Open").performKeyInput {
                withKeyDown(Key.CtrlLeft) {
                    keyDown(Key.O)
                }
                keyUp(Key.O)
            }
            onNodeWithText("cursor_target").performMouseInput {
                enter()
                exit()
            }

            verify { anyConstructed<SettingEditorViewModel>().start() }
            verify { anyConstructed<SettingEditorViewModel>().save() }
            verify { anyConstructed<SettingEditorViewModel>().openFile() }
        }
    }
}