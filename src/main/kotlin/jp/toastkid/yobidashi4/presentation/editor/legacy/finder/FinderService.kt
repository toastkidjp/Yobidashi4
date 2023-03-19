package jp.toastkid.yobidashi4.presentation.editor.legacy.finder

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea

class FinderService(
    private val editorArea: RSyntaxTextArea
) {

    private var lastFound = -1

    operator fun invoke(order: FindOrder) {
        if (order.invokeReplace) {
            replace(order)
            return
        }

        find(order)
    }

    private fun replace(order: FindOrder) {
        var indexOf = editorArea.text.indexOf(order.target, 0, order.caseSensitive.not())

        if (indexOf == -1) {
            //showMessage("'${order.target}' is not found.")
            return
        }

        while (indexOf != -1) {
            editorArea.replaceRange(order.replace, indexOf, indexOf + order.target.length)
            indexOf = editorArea.text.indexOf(order.target, indexOf + 1, order.caseSensitive.not())
        }
    }

    private fun find(order: FindOrder) {
        val indexOf = if (order.upper) {
            if (lastFound == -1) {
                lastFound = editorArea.text.length
            }
            editorArea.text.lastIndexOf(order.target, lastFound - 1, order.caseSensitive.not())
        } else {
            editorArea.text.indexOf(order.target, lastFound + 1, order.caseSensitive.not())
        }
        if (indexOf == -1) {
            //showMessage("'${order.target}' is not found.")
            return
        }
        lastFound = indexOf

        editorArea.selectionStart = indexOf
        editorArea.selectionEnd = indexOf + order.target.length
    }

}