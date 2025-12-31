package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.text.TextRange

class ControlAndLeftBracketCase {

    operator fun invoke(
        content: TextFieldState,
        selectionStartIndex: Int
    ): Boolean {
        if (content.text.length <= selectionStartIndex) {
            return false
        }
        val nextCharacter = content.text.get(selectionStartIndex)
        val target = when (nextCharacter) {
            '(' -> ')' to true
            ')' -> '(' to false
            '[' -> ']' to true
            ']' -> '[' to false
            '{' -> '}' to true
            '}' -> '{' to false
            '「' -> '」' to true
            '」' -> '「' to false
            '『' -> '』' to true
            '』' -> '『' to false
            else -> null
        } ?: return false

        val index = if (target.second) content.text.indexOf(target.first, selectionStartIndex + 1)
        else content.text.lastIndexOf(target.first, selectionStartIndex - 1)
        if (index == -1) {
            return false
        }

        content.edit {
            selection = TextRange(index, index + 1)
        }
        return true
    }

}