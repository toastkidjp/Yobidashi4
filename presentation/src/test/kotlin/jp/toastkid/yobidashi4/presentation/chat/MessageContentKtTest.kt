/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.foundation.ContextMenuState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.v2.runComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.chat.Source
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MessageContentKtTest {

    @RelaxedMockK
    private lateinit var viewModel: MessageContentViewModel
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { viewModel.storeImage(any()) } just Runs
        every { viewModel.openLink(any()) } just Runs
        every { viewModel.openLinkOnBackground(any()) } just Runs
        val contextMenuState = ContextMenuState()
        contextMenuState.status = ContextMenuState.Status.Open(Rect(4f, 25f, 116f, 57f))
        every { viewModel.contextMenuState() } returns contextMenuState
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun messageContent() {
        val source = Source("test", "https://www.yahoo.co.jp")

        runComposeUiTest {
            setContent {
                MessageContent(
                    "test\n* **test**\n* ***Good***",
                    null,
                    emptyList(),
                    Modifier,
                    viewModel
                )
                MessageContent(
                    "image",
                    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBASfqKgwAAAAASUVORK5CYII=",
                    listOf(source),
                    Modifier,
                    viewModel
                )
            }

            onNode(hasText("Store image")).performClick()

            verify { viewModel.storeImage(any()) }

            onNodeWithContentDescription("0,${source}", useUnmergedTree = true)
                .performMouseInput {
                    enter()
                    click()
                    longClick()
                }

            verify { viewModel.openLink(any()) }
            verify { viewModel.openLinkOnBackground(any()) }
        }
    }

}