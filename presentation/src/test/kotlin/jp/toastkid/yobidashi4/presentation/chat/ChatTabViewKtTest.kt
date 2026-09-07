/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
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
import jp.toastkid.yobidashi4.domain.model.chat.ChatMessage
import jp.toastkid.yobidashi4.domain.model.chat.GenerativeAiModel
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_chat
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import java.util.UUID

class ChatTabViewKtTest {

    @RelaxedMockK
    private lateinit var viewModel: ChatTabViewModel

    @RelaxedMockK
    private lateinit var messageContentViewModel: MessageContentViewModel

    @MockK
    private lateinit var tab: ChatTab

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { messageContentViewModel } bind(MessageContentViewModel::class)
                }
            )
        }

        every { viewModel.scrollState() } returns LazyListState(0)
        every { viewModel.messages() } returns listOf(
            ChatMessage("user", "test"),
            ChatMessage("model", "test")
        )
        coEvery { viewModel.launch(any(), any(), any(), any()) } just Runs
        every { viewModel.update(any()) } just Runs
        every { viewModel.switchImageGeneration() } just Runs
        every { viewModel.clearChat() } just Runs
        every { viewModel.clipText(any()) } just Runs
        every { viewModel.trySend() } just Runs
        every { viewModel.openModelChooser() } just Runs
        every { viewModel.closeModelChooser() } just Runs
        every { viewModel.openingModelChooser() } returns true
        every { viewModel.currentModelIcon() } returns Res.drawable.ic_chat
        every { viewModel.modelIcon(any()) } returns Res.drawable.ic_chat
        every { viewModel.currentModelLabel() } returns ""
        every { viewModel.label() } returns ""
        every { viewModel.focusRequester() } returns FocusRequester()
        every { viewModel.textInput() } returns TextFieldState("test")
        every { viewModel.name(any()) } returns ""
        every { viewModel.nameColor(any()) } returns Color.Red
        coEvery { viewModel.send() } just Runs
        every { tab.chat() } returns mockk()
        every { tab.scrollPosition() } returns 0
        every { tab.initialModel() } returns mockk()
        every { tab.initialQuestion() } returns "Initial question"
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun chatTabView() {
        val mutableSharedFlow = MutableSharedFlow<UUID>(extraBufferCapacity = 1)
        every { viewModel.sendEventFlow() } returns mutableSharedFlow
        val scrollEventFlow = MutableSharedFlow<Float>(extraBufferCapacity = 1)
        every { viewModel.scrollEventFlow() } returns scrollEventFlow

        runDesktopComposeUiTest {
            setContent {
                ChatTabView(tab, viewModel)
            }
            mainClock.advanceTimeByFrame()

            onNodeWithContentDescription("Input message box.", useUnmergedTree = true)
                .performClick()

            onNodeWithTag("clip_icon")
                .fetchSemanticsNode()
                .config.getOrNull(SemanticsActions.OnClick)?.action?.invoke()

            verify { viewModel.clipText(any()) }

            onNodeWithContentDescription("Chat list", useUnmergedTree = true)
                .performClick()
                .performKeyInput {
                    pressKey(Key.DirectionUp)
                    pressKey(Key.DirectionDown)
                }

            onNode(hasText("Clear chat"), useUnmergedTree = true)
                .performClick()
            //TODO verify { viewModel.clearChat() }

            onNodeWithContentDescription("Model chooser", useUnmergedTree = true)
                .performClick()

            val contentDescription = "chooserItem-${GenerativeAiModel.GEMINI_2_5_FLASH_LITE_WITHOUT_WEB_GROUNDING.label()}"
            onNodeWithContentDescription(contentDescription, useUnmergedTree = true)
                .performMouseInput {
                    click()
                }

            verify { viewModel.sendEventFlow() }
            mutableSharedFlow.tryEmit(UUID.randomUUID())

            scrollEventFlow.tryEmit(1f)

            onNodeWithContentDescription("Send chat", useUnmergedTree = true)
                .performClick()
            //TODO verify { viewModel.trySend() }
        }
    }

}