package jp.toastkid.yobidashi4.domain.service.tool.calculator

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SimpleCalculatorTest {

    @InjectMockKs
    private lateinit var calculator: SimpleCalculator

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testInvoke() {
        assertEquals(2.0, calculator.invoke("1+1"))
        assertEquals(4.4682, calculator.invoke("2.2341*2"))
        assertEquals(51.0, calculator.invoke("1500*0.03+6"))
        assertEquals(525.00, calculator.invoke("1500/3 + 25"))
        assertEquals(525.00, calculator.invoke("1,500/3 + 25"))
    }
}