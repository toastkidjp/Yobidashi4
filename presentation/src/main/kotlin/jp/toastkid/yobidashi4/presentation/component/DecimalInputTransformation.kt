/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer

class DecimalInputTransformation : InputTransformation {

    override fun TextFieldBuffer.transformInput() {
        if (this.length == 0) {
            return
        }

        // 変更後のテキスト全体が数値として妥当か（または空か）をチェック
        val newText = asCharSequence().toString()
        val isValid = newText.count { it == '.' } <= 1
                && newText.all { isDecimalInputCharacter(it) }

        if (!isValid) {
            revertAllChanges() // 条件に合わない場合は入力を差し戻す
        }
    }

    private fun isDecimalInputCharacter(ch: Char): Boolean =
        ch.isDigit() || ch == '.' || ch == ','

}