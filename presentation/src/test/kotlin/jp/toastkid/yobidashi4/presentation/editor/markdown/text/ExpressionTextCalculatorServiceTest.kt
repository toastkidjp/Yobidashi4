package jp.toastkid.yobidashi4.presentation.editor.markdown.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ExpressionTextCalculatorServiceTest {

    private lateinit var subject: ExpressionTextCalculatorService
    
    @BeforeEach
    fun setUp() {
        subject = ExpressionTextCalculatorService()
    }

    @Test
    fun appendLineBreakIfNeed() {
        assertEquals("4.4682\n", subject.invoke("2.2341*2\n"))
    }

    @ParameterizedTest
    @CsvSource(
        "2.2341*2, 4.4682",
        "1+1, 2",
        "1500*0.03+6, 51",
        "1500/3 + 25, 525",
        "1_500/3 + 25, 525",
        "Good+day, Good+day",
        "11+(2, 11+(2",
    )
    fun testInvoke(input: String, expected: String) {
        assertEquals(expected, subject.invoke(input.replace("_", ",")))
    }

}