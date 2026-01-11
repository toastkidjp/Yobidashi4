package jp.toastkid.yobidashi4.presentation.converter

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import jp.toastkid.yobidashi4.domain.service.converter.TwoStringConverterService

class TwoValueConverterBoxViewModel(private val unixTimeConverterService: TwoStringConverterService) {

    private val firstInput = (TextFieldState(unixTimeConverterService.defaultFirstInputValue()))

    private val secondInput = (TextFieldState(unixTimeConverterService.defaultSecondInputValue()))

    fun firstInput() = firstInput

    fun onFirstValueChange() {
        unixTimeConverterService.firstInputAction(firstInput.text.toString())
            ?.let(secondInput::setTextAndPlaceCursorAtEnd)
    }

    fun secondInput() = secondInput

    fun onSecondValueChange() {
        unixTimeConverterService.secondInputAction(secondInput.text.toString())
            ?.let(firstInput::setTextAndPlaceCursorAtEnd)
    }

    fun clearFirstInput() {
        firstInput.clearText()
    }

    fun clearSecondInput() {
        secondInput.clearText()
    }

}