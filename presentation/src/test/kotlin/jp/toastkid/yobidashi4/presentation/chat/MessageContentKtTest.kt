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
import androidx.compose.ui.test.runComposeUiTest
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.chat.Source
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MessageContentKtTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(MessageContentViewModel::class)
        every { anyConstructed<MessageContentViewModel>().storeImage(any()) } just Runs
        every { anyConstructed<MessageContentViewModel>().openLink(any()) } just Runs
        every { anyConstructed<MessageContentViewModel>().openLinkOnBackground(any()) } just Runs
        val contextMenuState = ContextMenuState()
        contextMenuState.status = ContextMenuState.Status.Open(Rect(4f, 25f, 116f, 57f))
        every { anyConstructed<MessageContentViewModel>().contextMenuState() } returns contextMenuState
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
                    Modifier
                )
                MessageContent(
                    "image",
                    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBASfqKgwAAAAASUVORK5CYII=",
                    listOf(source),
                    Modifier
                )
            }

            onNode(hasText("Store image")).performClick()

            verify { anyConstructed<MessageContentViewModel>().storeImage(any()) }

            onNodeWithContentDescription("0,${source}", useUnmergedTree = true)
                .performMouseInput {
                    enter()
                    click()
                    longClick()
                }

            verify { anyConstructed<MessageContentViewModel>().openLink(any()) }
            verify { anyConstructed<MessageContentViewModel>().openLinkOnBackground(any()) }
        }
    }

}