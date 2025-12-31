package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.text.TextRange

class SelectedTextConversion {

    operator fun invoke(
        content: TextFieldState,
        selectionStartIndex: Int,
        selectionEndIndex: Int,
        conversion: (String) -> String?
    ): Boolean {
        val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
        if (selected.isEmpty()) {
            return false
        }

        val converted = conversion(selected) ?: return false

        content.edit {
            replace(selectionStartIndex, selectionEndIndex, converted)
            selection = TextRange(selectionStartIndex, selectionStartIndex + converted.length)
        }

        return true
    }

}