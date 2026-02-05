package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.insert
import java.math.BigDecimal
import java.text.DecimalFormat

class DecimalVisualTransformation : OutputTransformation {

    private val formatter = DecimalFormat("#,###.##")

    private fun toDecimal(input: String): BigDecimal? {
        return input.toBigDecimalOrNull();
    }

    override fun TextFieldBuffer.transformOutput() {
        val input = asCharSequence().toString()
        val decimal = toDecimal(input)
        val useFormatter = decimal != null && decimal != BigDecimal.ZERO && !input.contains(".")
        if (useFormatter.not() || input.length <= 3) {
            return
        }

        val formatted = formatter.format(decimal)
        var index = formatted.indexOf(",")
        while (index != -1) {
            insert(index, ",")
            index = formatted.indexOf(",", index + 1)
        }
    }

}