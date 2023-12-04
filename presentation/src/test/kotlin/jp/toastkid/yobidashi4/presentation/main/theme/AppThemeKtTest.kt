package jp.toastkid.yobidashi4.presentation.main.theme

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import org.junit.jupiter.api.Test

class AppThemeKtTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun lightModeCase() {
        runDesktopComposeUiTest {
            setContent {
                AppTheme(darkTheme = false) {

                }
            }
        }
    }

}