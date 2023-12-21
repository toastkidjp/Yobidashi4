package jp.toastkid.yobidashi4.domain.model.tab

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class NumberPlaceGameTabTest {

    @Test
    fun test() {
        val tab = NumberPlaceGameTab()
        assertNotNull(tab.title())
        assertTrue(tab.closeable())
        assertTrue(tab.iconPath()?.startsWith("images/icon/") ?: false)
    }

}