package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GlowingButtonKtTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun glowingButton() {
        runComposeUiTest {
            setContent {
                GlowingButton(modifier = Modifier.clickable {
                    assertTrue(true)
                }) {
                    Text("test")
                }
            }

            onNodeWithText("test").performClick()
        }
    }

}