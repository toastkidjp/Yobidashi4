package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runComposeUiTest
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class HoverHighlightDropdownMenuItemKtTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun test() {
        val countDownLatch = CountDownLatch(1)
        runComposeUiTest {
            setContent {
                HoverHighlightDropdownMenuItem("test") {
                    countDownLatch.countDown()
                }
            }

            onNode(hasText("test"), useUnmergedTree = true)
                .performMouseInput {
                    enter()
                    exit()
                    click()
                }

            countDownLatch.await(1, TimeUnit.SECONDS)
        }
    }

}