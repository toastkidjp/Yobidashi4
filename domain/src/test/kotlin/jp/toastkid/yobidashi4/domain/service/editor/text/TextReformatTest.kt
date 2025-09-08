package jp.toastkid.yobidashi4.domain.service.editor.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TextReformatTest {

    private lateinit var subject: TextReformat

    @BeforeEach
    fun setUp() {
        subject = TextReformat()
    }

    @Test
    fun invoke() {
        assertTrue(subject.invoke("").isEmpty())

        assertEquals("text", subject.invoke("  text"))

        assertEquals(
            """
a
  b
    c""",
            subject.invoke(
                """
            a
              b
                c"""
            )
        )
    }

}
