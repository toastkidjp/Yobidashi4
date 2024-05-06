package jp.toastkid.yobidashi4.presentation.time

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import org.junit.jupiter.api.Test

class WorldTimeViewKtTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun worldTimeView() {
        runDesktopComposeUiTest {
            setContent {
                WorldTimeView(Modifier)
            }
        }
    }
}