package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import org.junit.jupiter.api.Test

class MessageContentKtTest {

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
        }
    }

}