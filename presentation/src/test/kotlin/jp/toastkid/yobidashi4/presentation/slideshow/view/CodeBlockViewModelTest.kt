package jp.toastkid.yobidashi4.presentation.slideshow.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CodeBlockViewModelTest {

    private lateinit var subject: CodeBlockViewModel

    @BeforeEach
    fun setUp() {
        subject = CodeBlockViewModel()
    }

    @Test
    fun maxHeight() {
        println(subject.maxHeight(16.sp))
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun verticalScrollState() {
        assertEquals(0.0f, subject.verticalScrollState().offset)
    }

    @Test
    fun horizontalScrollState() {
        assertEquals(0, subject.horizontalScrollState().value)
    }

    @Test
    fun transform() {
        subject.transform(AnnotatedString("test"))
    }

    @Test
    fun onValueChange() {
        assertTrue(subject.content().text.isEmpty())

        subject.onValueChange(TextFieldValue("Just beat It\nBeat It\nBeat It"))

        assertEquals("Just beat It\nBeat It\nBeat It", subject.content().text)
        println(subject.lineNumberTexts())
        assertEquals(3, subject.lineNumberTexts().size)
    }

    @Test
    fun start() {
        subject.start("test")

        assertEquals("test", subject.content().text)
    }

}