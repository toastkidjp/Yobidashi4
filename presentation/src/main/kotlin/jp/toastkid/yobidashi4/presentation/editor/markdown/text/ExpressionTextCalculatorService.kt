package jp.toastkid.yobidashi4.presentation.editor.markdown.text

import java.util.regex.Pattern
import jp.toastkid.yobidashi4.domain.service.tool.calculator.SimpleCalculator

class ExpressionTextCalculatorService {

    private val calculator = SimpleCalculator()

    operator fun invoke(it: String): String {
        val appendLineBreakIfNeed = if (it.endsWith("\n")) "\n" else ""
        val doubleResult = calculator.invoke(it.trimEnd())?.toString() ?: return it
        val converted =
            if (pattern.matcher(doubleResult).find()) doubleResult.substring(0, doubleResult.lastIndexOf("."))
            else doubleResult
        return "${converted}$appendLineBreakIfNeed"
    }

}

private val pattern = Pattern.compile("\\.0*0$")
