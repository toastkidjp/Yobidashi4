package jp.toastkid.yobidashi4.domain.model.tab

import kotlinx.coroutines.flow.emptyFlow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TabTest {

    @Test
    fun test() {
        val tab = object : Tab {
            override fun title(): String = "test"
        }

        assertEquals("test", tab.title())
        assertTrue( tab.closeable())
        assertEquals(emptyFlow<Long>(), tab.update())
        assertNull(tab.iconPath())
    }

}