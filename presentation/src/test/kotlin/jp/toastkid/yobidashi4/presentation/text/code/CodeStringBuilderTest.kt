package jp.toastkid.yobidashi4.presentation.text.code

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CodeStringBuilderTest {

    private lateinit var subject: CodeStringBuilder

    @BeforeEach
    fun setUp() {
        subject = CodeStringBuilder()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun invoke() {
        val code = """
public class Code {
                
    public static void main(final String[] args) {
        System.out.println("test 001.");
    }

}
            """

        val annotatedString = subject.invoke(code)

        assertEquals(code, annotatedString.text)
        assertEquals(163, annotatedString.spanStyles.size)
        assertTrue(annotatedString.paragraphStyles.isEmpty())
    }

}