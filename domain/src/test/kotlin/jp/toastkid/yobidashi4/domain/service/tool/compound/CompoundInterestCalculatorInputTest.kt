package jp.toastkid.yobidashi4.domain.service.tool.compound

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class CompoundInterestCalculatorInputTest {

    @Test
    fun test() {
        assertNull(CompoundInterestCalculatorInput.from(null, null, null, null))
        assertNull(CompoundInterestCalculatorInput.from("", null, null, null))
        assertNull(CompoundInterestCalculatorInput.from("", "", null, null))
        assertNull(CompoundInterestCalculatorInput.from("", "", "", null))
        assertNull(CompoundInterestCalculatorInput.from("", "", "", ""))
    }

}