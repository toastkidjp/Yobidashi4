package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Test

class DividersKtTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun test() {
        runDesktopComposeUiTest {
            setContent {
                VerticalDivider(1.dp, modifier = Modifier)
            }
        }
    }

}