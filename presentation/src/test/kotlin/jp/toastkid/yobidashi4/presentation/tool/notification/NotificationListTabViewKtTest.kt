/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.tool.notification

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import jp.toastkid.yobidashi4.presentation.tool.notification.viewmodel.NotificationListTabViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class NotificationListTabViewKtTest {

    @RelaxedMockK
    private lateinit var viewModel: NotificationListTabViewModel
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { viewModel.scrollEventFlow() } returns MutableStateFlow<Int>(0)
        every { viewModel.listState() } returns LazyListState()
        every { viewModel.start(Dispatchers.IO) } just Runs
        every { viewModel.deleteAt(any()) } just Runs
        every { viewModel.update(any(), any(), any(), any()) } just Runs
        every { viewModel.focusRequester() } returns FocusRequester()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun notificationListTabView() {
        val item = NotificationEvent.makeDefault()
        every { viewModel.items() } returns mutableListOf(item)

        runDesktopComposeUiTest {
            setContent {
                NotificationListTabView(viewModel)
            }

            onNode(hasText("Update"), true).onParent().performClick()
            verify { viewModel.update(any(), any(), any(), any()) }

            onNode(hasText("x"), true).onParent().performClick().performKeyInput { pressKey(Key.DirectionUp) }
            verify { viewModel.deleteAt(any()) }

            onNode(hasText(item.title), true)
                .performMouseInput {
                    enter()
                    exit()
                }
                .performTextInput("NewTitle")

            onNode(hasText(item.text), true)
                .performTextInput("NewText")

            onNode(hasText(item.dateTimeString()), true)
                .performTextInput("2024-02-02")
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun notFirst() {
        every { viewModel.listState() } returns LazyListState(2)
        val item = NotificationEvent.makeDefault()
        every { viewModel.items() } returns mutableListOf(
            item,
            NotificationEvent("2nd", "2nd", LocalDateTime.now())
        )

        runDesktopComposeUiTest {
            setContent {
                NotificationListTabView(viewModel)
            }

            onAllNodes(hasText("Update"), true)
                .get(1)
                .onParent()
                .performClick()
                .performKeyInput {
                    pressKey(Key.DirectionUp, 1000L)
                }
        }
    }

}