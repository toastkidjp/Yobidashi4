package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor

class ClipboardFetcher(private val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard) {

    operator fun invoke(): String? {
        val transferData = clipboard.getContents(this)
        return if (transferData == null || !transferData.isDataFlavorSupported(DataFlavor.stringFlavor)) null
        else transferData.getTransferData(DataFlavor.stringFlavor).toString()
    }
}