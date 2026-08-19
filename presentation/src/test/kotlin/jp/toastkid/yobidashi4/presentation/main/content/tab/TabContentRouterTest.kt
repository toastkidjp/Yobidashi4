package jp.toastkid.yobidashi4.presentation.main.content.tab

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import org.junit.jupiter.api.Test

class TabContentRouterTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tabContentRouter() {
        runDesktopComposeUiTest {
            setContent {
                TabContentRouter(null)
            }
        }
    }

}