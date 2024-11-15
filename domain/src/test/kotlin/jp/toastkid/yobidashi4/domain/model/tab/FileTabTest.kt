package jp.toastkid.yobidashi4.domain.model.tab

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FileTabTest {

    private lateinit var tab: FileTab

    @BeforeEach
    fun setUp() {
        tab = FileTab("normal", listOf())
    }

    @Test
    fun test() {
        assertNotNull(tab.title())
        assertTrue(tab.closeable())
        assertNull(tab.iconPath())
    }

    @Test
    fun music() {
        tab = FileTab("music", emptyList(), FileTab.Type.MUSIC)
        assertNotNull(tab.title())
        assertTrue(tab.closeable())
        assertNull(tab.iconPath())
    }

}