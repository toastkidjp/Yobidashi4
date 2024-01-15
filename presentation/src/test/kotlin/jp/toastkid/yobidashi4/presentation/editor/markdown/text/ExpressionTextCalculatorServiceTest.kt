package jp.toastkid.yobidashi4.presentation.editor.markdown.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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

    @Test
    fun invoke() {
        assertEquals("4.4682", subject.invoke("2.2341*2"))
        assertEquals("2", subject.invoke("1+1"))
        assertEquals("51", subject.invoke("1500*0.03+6"))
        assertEquals("525", subject.invoke("1500/3 + 25"))
        assertEquals("525", subject.invoke("1,500/3 + 25"))
        assertEquals("Good+day", subject.invoke("Good+day"))
        assertEquals("11+(2", subject.invoke("11+(2"))
    }
}