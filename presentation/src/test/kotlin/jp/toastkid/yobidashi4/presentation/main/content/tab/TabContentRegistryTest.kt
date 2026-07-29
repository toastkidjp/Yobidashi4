package jp.toastkid.yobidashi4.presentation.main.content.tab

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull

class TabContentRegistryTest {

    // テスト用のダミー Tab 実装
    private class DummyTab : Tab {
        override fun title() = "Dummy"
    }

    private class UnregisteredTab : Tab {
        override fun title() = "Unregistered"
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testRenderNullTab() {
        val registry = TabContentRegistry.Builder().build()

        runDesktopComposeUiTest {
            setContent {
                registry.Render(null)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testRenderUnregisteredTab() {
        var called = false
        val registry = TabContentRegistry.Builder()
            .register(DummyTab::class) {
                called = true
            }
            .build()

        runDesktopComposeUiTest {
            setContent {
                registry.Render(UnregisteredTab())
            }
        }

        assertTrue(!called, "未登録のTabに対するレンダラーは呼び出されないこと")
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testRenderRegisteredTab() {
        var passedTab: DummyTab? = null
        val dummyInstance = DummyTab()

        val registry = TabContentRegistry.Builder()
            .register(DummyTab::class) { tab ->
                passedTab = tab
            }
            .build()

        runDesktopComposeUiTest {
            setContent {
                registry.Render(dummyInstance)
            }
        }

        assertNotNull(passedTab)
        assertTrue(passedTab === dummyInstance, "登録されたコンポーザブルに正しい Tab インスタンスが渡されること")
    }

}
