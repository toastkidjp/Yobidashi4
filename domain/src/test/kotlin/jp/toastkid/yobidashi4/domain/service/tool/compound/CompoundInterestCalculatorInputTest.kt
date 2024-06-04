package jp.toastkid.yobidashi4.domain.service.tool.compound

import org.junit.jupiter.api.Assertions.assertNotNull
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
        assertNull(CompoundInterestCalculatorInput.from("1", null, null, null))
        assertNull(CompoundInterestCalculatorInput.from("2", "2", null, null))
        assertNull(CompoundInterestCalculatorInput.from("3", "3", "2", null))
        assertNotNull(CompoundInterestCalculatorInput.from("4", "4", "2", "1"))
    }

}