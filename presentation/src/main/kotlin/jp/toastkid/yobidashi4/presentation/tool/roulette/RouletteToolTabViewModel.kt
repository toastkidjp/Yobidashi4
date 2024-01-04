package jp.toastkid.yobidashi4.presentation.tool.roulette

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService

class RouletteToolTabViewModel {

    private val input = mutableStateOf(TextFieldValue("Cake\nCoffee\nScone\nTea"))

    private val result = mutableStateOf("")

    fun input() = input.value

    fun onValueChange(it: TextFieldValue) {
        input.value = it
    }

    fun clearInput() {
        input.value = TextFieldValue()
    }

    fun roulette() {
        result.value = input.value.text.split("\n").random()
    }

    fun result() = result.value

    fun clipResult() {
        if (result.value.isBlank()) {
            return
        }

        ClipboardPutterService().invoke(result.value)
    }

}