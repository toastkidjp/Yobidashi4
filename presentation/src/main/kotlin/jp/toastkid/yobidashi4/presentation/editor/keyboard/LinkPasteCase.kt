package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.insert
import androidx.compose.ui.text.TextRange
import jp.toastkid.yobidashi4.domain.service.editor.LinkDecoratorService
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardFetcher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.min

class LinkPasteCase : KoinComponent {

    private val linkDecoratorService: LinkDecoratorService by inject()

    operator fun invoke(
        content: TextFieldState,
        selectionStartIndex: Int,
        selectionEndIndex: Int,
        selectedTextConversion: SelectedTextConversion,
    ): Boolean {
        val selected = content.text.substring(selectionStartIndex, selectionEndIndex)
        if (isUrl(selected)) {
            selectedTextConversion(content, selectionStartIndex, selectionEndIndex, {
                linkDecoratorService.invoke(selected)
            })
            return true
        }

        val clipped = ClipboardFetcher().invoke() ?: return false
        if (isUrl(clipped)) {
            val decoratedLink = linkDecoratorService.invoke(clipped)

            content.edit {
                insert(selectionStartIndex, decoratedLink)
                selection = TextRange(min(length, selectionStartIndex + decoratedLink.length))
            }

        }

        return true
    }

    private fun isUrl(text: String) = text.startsWith("http://") || text.startsWith("https://")

}
