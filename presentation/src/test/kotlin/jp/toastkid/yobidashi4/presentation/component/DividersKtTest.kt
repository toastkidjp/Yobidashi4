package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Test

class DividersKtTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun test() {
        runComposeUiTest {
            setContent {
                VerticalDivider(1.dp, modifier = Modifier)
                VerticalDivider(1.dp, Color.Red, modifier = Modifier)
            }
        }
    }

}