package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.math.BigDecimal
import java.text.DecimalFormat

class DecimalVisualTransformation : VisualTransformation {

    private val formatter = DecimalFormat("#,###.##")

    override fun filter(text: AnnotatedString): TransformedText {
        val decimal = toDecimal(text.text)
        val useFormatter = decimal != null && decimal != BigDecimal.ZERO && !text.contains(".")
        val formatted = if (useFormatter) AnnotatedString(formatter.format(decimal)) else text
        val offsetMapping = if (!useFormatter) OffsetMapping.Identity else object : OffsetMapping {

            override fun originalToTransformed(offset: Int): Int {
                val totalSeparatorCount = (text.length - 1) / 3
                val rightSeparatorCount = (text.length - 1 - offset) / 3
                val leftSeparatorCount = totalSeparatorCount - rightSeparatorCount
                return offset + leftSeparatorCount
            }

            override fun transformedToOriginal(offset: Int): Int {
                val totalSeparatorCount = (text.length - 1) / 3
                val rightSeparatorCount = (formatted.length - offset) / 4
                val leftSeparatorCount = totalSeparatorCount - rightSeparatorCount
                return offset - leftSeparatorCount
            }

        }

        return TransformedText(formatted, offsetMapping = offsetMapping)
    }

    private fun toDecimal(input: String): BigDecimal? {
        return input.toBigDecimalOrNull();
    }

}