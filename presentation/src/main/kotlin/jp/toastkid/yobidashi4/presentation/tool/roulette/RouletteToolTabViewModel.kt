package jp.toastkid.yobidashi4.presentation.tool.roulette

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService

class RouletteToolTabViewModel {

    private val input = TextFieldState("Cake\nCoffee\nScone\nTea")

    private val result = mutableStateOf("")

    fun input() = input

    fun clearInput() {
        input.clearText()
    }

    fun roulette() {
        result.value = input.text.split("\n").random()
    }

    fun result() = result.value

    fun clipResult() {
        if (result.value.isBlank()) {
            return
        }

        ClipboardPutterService().invoke(result.value)
    }

    fun onKeyEvent(it: KeyEvent): Boolean {
        if (input.composition == null && it.isCtrlPressed && it.key == Key.Enter) {
            roulette()
            return true
        }

        return false
    }

}