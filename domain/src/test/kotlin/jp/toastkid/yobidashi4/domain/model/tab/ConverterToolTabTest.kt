package jp.toastkid.yobidashi4.domain.model.tab

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ConverterToolTabTest {

    private lateinit var tab: ConverterToolTab

    @BeforeEach
    fun setUp() {
        tab = ConverterToolTab()
    }

    @Test
    fun title() {
        assertEquals("Converter", tab.title())
    }

    @Test
    fun closeable() {
        assertTrue(tab.closeable())
    }

    @Test
    fun iconPath() {
        assertTrue(tab.iconPath()?.startsWith("images/icon/") ?: false)
    }

}