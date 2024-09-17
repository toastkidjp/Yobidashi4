package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

class SelectedTextConversion {

    operator fun invoke(
        content: TextFieldValue,
        selectionStartIndex: Int,
        selectionEndIndex: Int,
        conversion: (String) -> String?,
        setNewContent: (TextFieldValue) -> Unit
    ): Boolean {
        val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
        if (selected.isEmpty()) {
            return false
        }

        val converted = conversion(selected) ?: return false
        val newText = StringBuilder(content.text)
            .replace(
                selectionStartIndex,
                selectionEndIndex,
                converted
            )
            .toString()

        setNewContent(
            TextFieldValue(
                newText,
                TextRange(selectionStartIndex, selectionStartIndex + converted.length),
                content.composition
            )
        )

        return true
    }

}
