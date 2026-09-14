package jp.toastkid.yobidashi4.presentation.main.content.tab

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import io.mockk.mockk
import org.junit.jupiter.api.Test

class TabContentRouterTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun defaultParameter() {
        runDesktopComposeUiTest {
            setContent {
                TabContentRouter(null)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tabContentRouter() {
        val registry = TabContentRegistry.Builder().build()

        runDesktopComposeUiTest {
            setContent {
                TabContentRouter(mockk(), registry = registry)
            }
        }
    }

}