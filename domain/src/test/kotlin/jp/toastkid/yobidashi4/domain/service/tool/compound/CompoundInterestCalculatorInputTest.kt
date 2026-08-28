package jp.toastkid.yobidashi4.domain.service.tool.compound

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class CompoundInterestCalculatorInputTest {

    @ParameterizedTest
    @CsvSource(
        "null, null, null, null",
        "'', null, null, null",
        "'', '', null, null",
        "'', '', '', null",
        "'', '', '', ''",
        "1, null, null, null",
        "2, 2, null, null",
        "3, 3, 2, null",
        "test, 4, '', ''",
        nullValues = ["null"]
    )
    fun nullCases(
        capitalInput: String?,
        installmentInput: String?,
        annualInterestInput: String?,
        yearInput: String?
    ) {
        assertNull(CompoundInterestCalculatorInput.from(capitalInput, installmentInput, annualInterestInput, yearInput))
    }

    @Test
    fun test() {
        assertNotNull(CompoundInterestCalculatorInput.from("4", "4", "2", "1"))
    }

}