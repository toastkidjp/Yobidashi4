package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.LinkDecoratorService
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardFetcher

class LinkPasteCase {

    operator fun invoke(
        content: TextFieldValue,
        selectionStartIndex: Int,
        selectionEndIndex: Int,
        selectedTextConversion: SelectedTextConversion,
        setNewContent: (TextFieldValue) -> Unit
    ): Boolean {
        val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
        if (isUrl(selected)) {
            selectedTextConversion(content, selectionStartIndex, selectionEndIndex, {
                LinkDecoratorService().invoke(selected)
            }, setNewContent)
            return true
        }

        val clipped = ClipboardFetcher().invoke() ?: return false
        if (isUrl(clipped)) {
            val decoratedLink = LinkDecoratorService().invoke(clipped)
            val newText = StringBuilder(content.text)
                .insert(
                    selectionStartIndex,
                    decoratedLink
                )
                .toString()
            setNewContent(
                TextFieldValue(
                    newText,
                    TextRange(selectionStartIndex + decoratedLink.length + 1),
                    content.composition
                )
            )
        }

        return true
    }

    private fun isUrl(text: String) = text.startsWith("http://") || text.startsWith("https://")

}