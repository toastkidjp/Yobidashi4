package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import jp.toastkid.yobidashi4.domain.service.editor.LinkDecoratorService
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardFetcher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LinkPasteCase : KoinComponent {

    private val linkDecoratorService: LinkDecoratorService by inject()

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
                linkDecoratorService.invoke(selected)
            }, setNewContent)
            return true
        }

        val clipped = ClipboardFetcher().invoke() ?: return false
        if (isUrl(clipped)) {
            val decoratedLink = linkDecoratorService.invoke(clipped)
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