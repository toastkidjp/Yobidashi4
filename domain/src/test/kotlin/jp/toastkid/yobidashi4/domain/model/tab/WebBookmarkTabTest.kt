package jp.toastkid.yobidashi4.domain.model.tab

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class WebBookmarkTabTest {

    @Test
    fun test() {
        val tab = WebBookmarkTab()
        assertNotNull(tab.title())
        assertNotNull(tab.iconPath())
        assertTrue(tab.closeable())
    }

}