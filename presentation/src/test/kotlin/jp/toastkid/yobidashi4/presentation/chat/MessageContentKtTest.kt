package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.foundation.ContextMenuState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MessageContentKtTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(MessageContentViewModel::class)
        every { anyConstructed<MessageContentViewModel>().storeImage(any()) } just Runs
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
        runComposeUiTest {
            setContent {
                MessageContent(
                    "test\n* **test**\n* ***Good***",
                    null,
                    Modifier
                )
                MessageContent(
                    "image",
                    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBASfqKgwAAAAASUVORK5CYII=",
                    Modifier
                )
            }

            onNode(hasText("Store image")).performClick()

            verify { anyConstructed<MessageContentViewModel>().storeImage(any()) }
        }
    }

}