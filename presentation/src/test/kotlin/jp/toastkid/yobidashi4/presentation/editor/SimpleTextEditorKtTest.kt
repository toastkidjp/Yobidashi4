/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import androidx.compose.ui.unit.em
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.editor.data.LineNumber
import jp.toastkid.yobidashi4.presentation.editor.viewmodel.TextEditorViewModel
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SimpleTextEditorKtTest {

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var setting: Setting
    
    @RelaxedMockK
    private lateinit var viewModel: TextEditorViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { mainViewModel.darkMode() } returns false
        every { mainViewModel.updateEditorContent(any(), any(), any(), any(), any()) } just Runs
        every { setting.editorConversionLimit() } returns 3_000
        every { setting.editorFontSize() } returns 16
        every { setting.editorLineHeight() } returns 1.5f

        every { viewModel.launchTab(any()) } just Runs
        coEvery { viewModel.initialScroll(any()) } just Runs
        every { viewModel.onClickLineNumber(any()) } just Runs
        every { viewModel.dispose() } just Runs
        coEvery { viewModel.adjustLineNumberState() } just Runs
        every { viewModel.content() } returns TextFieldState()
        every { viewModel.fontSize() } returns 16
        every { viewModel.lineHeight() } returns 16.em.value
        every { viewModel.currentLineOffset() } returns Offset.Zero
        every { viewModel.currentLineHighlightColor() } returns Color.Red
        every { viewModel.getHighlightSize() } returns Size(Float.MAX_VALUE, 37.em.value)
        every { viewModel.verticalScrollState() } returns ScrollState(0)
        every { viewModel.lineNumberScrollState() } returns ScrollState(0)
        every { viewModel.scrollEventFlow() } returns MutableSharedFlow<Float>(extraBufferCapacity = 1)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun simpleTextEditor() {
        val setStatus = mockk<(String) -> Unit>()
        every { setStatus(any()) } just Runs
        val textFieldValue = TextFieldState("test_content\nSecond line\n3rd line")
        every { viewModel.content() } returns textFieldValue
        val mutableSharedFlow = MutableSharedFlow<Float>(extraBufferCapacity = 1)
        every { viewModel.showLineNumber() } returns true
        every { viewModel.scrollEventFlow() } returns mutableSharedFlow
        every { viewModel.lineNumbers() } returns listOf(
            LineNumber(1, "1"),
            LineNumber(2, "2"),
            LineNumber(3, "3"),
            LineNumber(4, "4"),
        )

        runDesktopComposeUiTest {
            setContent {
                SimpleTextEditor(
                    EditorTab(mockk()),
                    setStatus,
                    viewModel = viewModel
                )
            }

            onNodeWithContentDescription("Editor input area", useUnmergedTree = true)
                .assertExists("Not exists!")
                .performClick()
                .performKeyInput {
                    pressKey(Key.A, 1000L)
                }
                .performTextInput("test new value")
            verify { setStatus(any()) }

            onNodeWithContentDescription("Line number 1", useUnmergedTree = true)
                .performClick()
            verify { viewModel.onClickLineNumber(any()) }
            verify { viewModel.scrollEventFlow() }
            mutableSharedFlow.tryEmit(1f)
            mainClock.advanceTimeBy(1000)
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun notShowLineNumber() {
        val setStatus = mockk<(String) -> Unit>()
        every { setStatus(any()) } just Runs
        val textFieldValue = TextFieldState("test_content\nSecond line\n3rd line")
        every { viewModel.content() } returns textFieldValue
        every { viewModel.showLineNumber() } returns false

        runDesktopComposeUiTest {
            setContent {
                SimpleTextEditor(
                    EditorTab(mockk()),
                    setStatus,
                    viewModel = viewModel
                )
            }
        }
    }

}