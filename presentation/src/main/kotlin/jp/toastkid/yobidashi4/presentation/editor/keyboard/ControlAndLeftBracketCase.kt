package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

class ControlAndLeftBracketCase {

    operator fun invoke(
        content: TextFieldValue,
        selectionStartIndex: Int,
        setNewContent: (TextFieldValue) -> Unit
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

        setNewContent(content.copy(selection = TextRange(index, index + 1)))
        return true
    }

}