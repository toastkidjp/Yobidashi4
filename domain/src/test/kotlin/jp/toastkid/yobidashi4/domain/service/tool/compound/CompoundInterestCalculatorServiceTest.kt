package jp.toastkid.yobidashi4.domain.service.tool.compound

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CompoundInterestCalculatorServiceTest {

    private lateinit var compoundInterestCalculatorService: CompoundInterestCalculatorService

    @BeforeEach
    fun setUp() {
        compoundInterestCalculatorService = CompoundInterestCalculatorService()
    }

    @Test
    fun test() {
        val result = compoundInterestCalculatorService.invoke(120000, 0.01, 10)
        assertEquals(3, result.header().size)
        assertEquals(10, result.itemArrays().size)
        assertEquals(Int::class.java, result.columnClass(1))
        assertNotNull(result.title())
        assertFalse(result.isEmpty())

        val tenYearsLater = result.get(10) ?: fail("This case does not allow null.")
        assertEquals(1212000, tenYearsLater.second)
        assertEquals(1255466, tenYearsLater.third)
    }

}