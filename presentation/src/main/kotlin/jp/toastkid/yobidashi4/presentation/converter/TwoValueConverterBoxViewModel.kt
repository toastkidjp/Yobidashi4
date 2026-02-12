/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.converter

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import jp.toastkid.yobidashi4.domain.service.converter.TwoStringConverterService

class TwoValueConverterBoxViewModel(private val unixTimeConverterService: TwoStringConverterService) {

    private val firstInput = (TextFieldState(unixTimeConverterService.defaultFirstInputValue()))

    private val secondInput = (TextFieldState(unixTimeConverterService.defaultSecondInputValue()))

    fun firstInput() = firstInput

    fun onFirstValueChange() {
        val result = unixTimeConverterService.firstInputAction(firstInput.text.toString()) ?: return

        secondInput.clearText()
        secondInput.edit { append(result) }
    }

    fun secondInput() = secondInput

    fun onSecondValueChange() {
        val result = unixTimeConverterService.secondInputAction(secondInput.text.toString()) ?: return

        firstInput.clearText()
        firstInput.edit { append(result) }
    }

    fun clearFirstInput() {
        firstInput.clearText()
    }

    fun clearSecondInput() {
        secondInput.clearText()
    }

}