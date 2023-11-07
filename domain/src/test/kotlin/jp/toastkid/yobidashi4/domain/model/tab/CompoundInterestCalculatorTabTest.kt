package jp.toastkid.yobidashi4.domain.model.tab

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class CompoundInterestCalculatorTabTest {

    @Test
    fun test() {
        val tab = CompoundInterestCalculatorTab()

        assertNotNull(tab.title())
        assertNotNull(tab.iconPath())
    }

}