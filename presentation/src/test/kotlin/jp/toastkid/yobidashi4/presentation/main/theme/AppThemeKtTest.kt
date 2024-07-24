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

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun darkModeCase() {
        runDesktopComposeUiTest {
            setContent {
                AppTheme(darkTheme = true) {

                }
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun defaultArgsCase() {
        runDesktopComposeUiTest {
            setContent {
                AppTheme {

                }
            }
        }
    }

}