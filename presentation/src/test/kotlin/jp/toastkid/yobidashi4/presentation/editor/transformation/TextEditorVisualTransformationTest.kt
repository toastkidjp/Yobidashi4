package jp.toastkid.yobidashi4.presentation.editor.transformation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TextEditorVisualTransformationTest {

    private lateinit var subject: TextEditorVisualTransformation

    @BeforeEach
    fun setUp() {
        subject = TextEditorVisualTransformation(mutableStateOf(TextFieldValue()), true)
    }

    @Test
    fun visualTransformation() {
        val transformedText = subject.filter(buildAnnotatedString { append("test") })
        assertEquals("test[EOF]", transformedText.text.text)
    }
    @Test
    fun overwriteWithStateContainingComposition() {
        val content = mutableStateOf(TextFieldValue("test"))
        subject = TextEditorVisualTransformation(content, true)
        val first = subject.filter(buildAnnotatedString { append("test") })

        // overwrite
        content.value = TextFieldValue("test", composition = TextRange.Zero)
        assertNotSame(first, subject.filter(buildAnnotatedString { append("test") }))
        assertNotSame(first, subject.filter(buildAnnotatedString { append("test2") }))

        content.value = TextFieldValue("test")
        assertNotSame(first, subject.filter(buildAnnotatedString { append("test2") }))
    }

    @Test
    fun useCache() {
        val content = mutableStateOf(TextFieldValue("test"))
        subject = TextEditorVisualTransformation(content, true)

        // use cache
        val first = subject.filter(buildAnnotatedString { append("test") })
        assertSame(first, subject.filter(buildAnnotatedString { append("test") }))
        // overwrite
        assertNotSame(first, subject.filter(buildAnnotatedString { append("test2") }))
    }

}