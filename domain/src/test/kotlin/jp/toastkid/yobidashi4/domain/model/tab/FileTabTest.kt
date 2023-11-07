package jp.toastkid.yobidashi4.domain.model.tab

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
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
        assertTrue(tab.iconPath()?.contains("search") ?: false)
    }

    @Test
    fun music() {
        tab = FileTab("music", emptyList(), false, FileTab.Type.MUSIC)
        assertNotNull(tab.title())
        assertFalse(tab.closeable())
        assertTrue(tab.iconPath()?.contains("music") ?: false)
    }

}