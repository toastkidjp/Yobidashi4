package jp.toastkid.yobidashi4.presentation.main.theme

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import org.junit.jupiter.api.Test

class AppThemeKtTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun lightModeCase() {
        runComposeUiTest {
            setContent {
                AppTheme(darkTheme = false) {

                }
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun darkModeCase() {
        runComposeUiTest {
            setContent {
                AppTheme(darkTheme = true) {

                }
            }
        }
    }

}