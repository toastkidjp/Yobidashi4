package jp.toastkid.yobidashi4.presentation.converter

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import jp.toastkid.yobidashi4.domain.service.converter.TwoStringConverterService

class TwoValueConverterBoxViewModel(private val unixTimeConverterService: TwoStringConverterService) {

    private val firstInput = mutableStateOf(TextFieldValue(unixTimeConverterService.defaultFirstInputValue()))

    private val secondInput = mutableStateOf(TextFieldValue(unixTimeConverterService.defaultSecondInputValue()))

    fun firstInput() = firstInput.value

    fun onFirstValueChange(it: TextFieldValue) {
        firstInput.value = it

        unixTimeConverterService.firstInputAction(firstInput.value.text)?.let { newValue ->
            secondInput.value = TextFieldValue(newValue)
        }
    }

    fun secondInput() = secondInput.value

    fun onSecondValueChange(it: TextFieldValue) {
        secondInput.value = it

        unixTimeConverterService.secondInputAction(secondInput.value.text)?.let { input ->
            firstInput.value = TextFieldValue(input)
        }
    }

    fun clearFirstInput() {
        firstInput.value = TextFieldValue()
    }

    fun clearSecondInput() {
        secondInput.value = TextFieldValue()
    }

}