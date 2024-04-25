package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.VisualTransformation
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DecimalVisualTransformationTest {

    private lateinit var subject: VisualTransformation

    @BeforeEach
    fun setUp() {
        subject = DecimalVisualTransformation()
    }

    @Test
    fun filter() {
        val willContainingComma = subject.filter(AnnotatedString("1000000"))
        Assertions.assertEquals("1,000,000", willContainingComma.text.text)
        Assertions.assertEquals(4, willContainingComma.offsetMapping.originalToTransformed(3))
        Assertions.assertEquals(2, willContainingComma.offsetMapping.transformedToOriginal(3))

        val containsDot = subject.filter(AnnotatedString("0.33343"))
        Assertions.assertEquals("0.33343", containsDot.text.text)
        Assertions.assertEquals(0, containsDot.offsetMapping.originalToTransformed(0))
        Assertions.assertEquals(1, containsDot.offsetMapping.transformedToOriginal(1))

        val zero = subject.filter(AnnotatedString("0"))
        Assertions.assertEquals("0", zero.text.text)
        Assertions.assertEquals(0, zero.offsetMapping.originalToTransformed(0))
        Assertions.assertEquals(1, zero.offsetMapping.transformedToOriginal(1))

        val transformedText = subject.filter(AnnotatedString("test"))
        Assertions.assertEquals("test", transformedText.text.text)
        Assertions.assertEquals(0, transformedText.offsetMapping.originalToTransformed(0))
        Assertions.assertEquals(1, transformedText.offsetMapping.transformedToOriginal(1))
    }

}