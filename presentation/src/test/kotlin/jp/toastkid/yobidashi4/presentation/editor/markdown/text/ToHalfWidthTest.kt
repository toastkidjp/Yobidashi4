package jp.toastkid.yobidashi4.presentation.editor.markdown.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ToHalfWidthTest {

    private lateinit var subject: ToHalfWidth

    @BeforeEach
    fun setUp() {
        subject = ToHalfWidth()
    }

    @Test
    fun invoke() {
        assertEquals("10月21日ABCホールにて", subject.invoke("１０月２１日ＡＢＣホールにて"))
    }

}
