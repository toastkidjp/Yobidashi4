package jp.toastkid.yobidashi4.presentation.compound

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import org.junit.jupiter.api.Test

class CompoundInterestCalculatorViewKtTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun compoundInterestCalculatorView() {
        runDesktopComposeUiTest {
            setContent {
                CompoundInterestCalculatorView()
            }
        }
    }
}