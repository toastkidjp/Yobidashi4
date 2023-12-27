package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import org.junit.jupiter.api.Test

class MemoryUsageBoxKtTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun memoryUsageBox() {
        runDesktopComposeUiTest {
            setContent {
                MemoryUsageBox()
            }
        }
    }

}