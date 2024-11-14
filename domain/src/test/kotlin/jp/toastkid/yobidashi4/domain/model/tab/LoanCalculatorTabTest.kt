package jp.toastkid.yobidashi4.domain.model.tab

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LoanCalculatorTabTest {

    @Test
    fun test() {
        val tab = LoanCalculatorTab()
        assertNotNull(tab.title())
        assertNull(tab.iconPath())
        assertTrue(tab.closeable())
    }

}