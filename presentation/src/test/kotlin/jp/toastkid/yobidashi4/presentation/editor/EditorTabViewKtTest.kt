/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.unit.em
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.markdown.Markdown
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.service.markdown.MarkdownParser
import jp.toastkid.yobidashi4.presentation.editor.viewmodel.TextEditorViewModel
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class EditorTabViewKtTest {

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var setting: Setting

    @RelaxedMockK
    private lateinit var textEditorViewModel: TextEditorViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier=null) { setting } bind(Setting::class)
                    single(qualifier=null) { textEditorViewModel } bind(TextEditorViewModel::class)
                }
            )
        }

        every { setting.editorConversionLimit() } returns 4500
        every { setting.editorFontSize() } returns 16
        every { setting.editorLineHeight() } returns 1.5f
        every { mainViewModel.darkMode() } returns false
        every { mainViewModel.updateEditorContent(any(), any(), any(), any(), any()) } just Runs
        every { mainViewModel.finderFlow() } returns emptyFlow()
        every { mainViewModel.setFindStatus(any()) } just Runs

        every { textEditorViewModel.content() } returns TextFieldState()
        every { textEditorViewModel.verticalScrollState() } returns ScrollState(0)
        every { textEditorViewModel.lineNumberScrollState() } returns ScrollState(0)
        every { textEditorViewModel.fontSize() } returns 16
        every { textEditorViewModel.lineHeight() } returns 16.em.value
        every { textEditorViewModel.scrollEventFlow() } returns MutableSharedFlow<Float>(extraBufferCapacity = 1)

        mockkConstructor(MarkdownParser::class)
        every { anyConstructed<MarkdownParser>().invoke(any()) } returns Markdown("test")
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun editorTabView() {
        val tab = EditorTab(mockk())

        runDesktopComposeUiTest {
            mainClock.autoAdvance = false

            setContent {
                EditorTabView(tab)
            }

            tab.switchPreview()

            mainClock.advanceTimeBy(2500L)
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun preview() {
        val tab = EditorTab(mockk())
        mockkConstructor(EditorTabViewModel::class)
        every { anyConstructed<EditorTabViewModel>().showPreview() } returns true
        every { anyConstructed<EditorTabViewModel>().preview() } returns Markdown("Test")

        runDesktopComposeUiTest {
            mainClock.autoAdvance = false

            setContent {
                EditorTabView(tab)
            }
            mainClock.advanceTimeBy(2500L)

            verify { anyConstructed<EditorTabViewModel>().showPreview() }
            verify { anyConstructed<EditorTabViewModel>().preview() }
        }
    }
}
