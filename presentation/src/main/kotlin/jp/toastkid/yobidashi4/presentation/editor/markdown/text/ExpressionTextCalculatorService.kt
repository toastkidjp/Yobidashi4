/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor.markdown.text

import jp.toastkid.yobidashi4.domain.service.tool.calculator.SimpleCalculator
import java.util.regex.Pattern

class ExpressionTextCalculatorService {

    private val calculator = SimpleCalculator()

    operator fun invoke(it: String): String {
        val result = calculator.invoke(it.trimEnd()) ?: return it
        val appendLineBreakIfNeed = if (it.endsWith("\n")) "\n" else ""
        val doubleResult = result.toString()
        val converted =
            if (pattern.matcher(doubleResult).find()) doubleResult.substring(0, doubleResult.lastIndexOf("."))
            else doubleResult
        return "${converted}$appendLineBreakIfNeed"
    }

}

private val pattern = Pattern.compile("\\.0*0$")
