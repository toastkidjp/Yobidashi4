package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import jp.toastkid.yobidashi4.domain.model.clipboard.TransferableImage

class ClipboardPutterService(private val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard) {

    operator fun invoke(string: String?) {
        clipboard.setContents(StringSelection(string)) { _, _ -> }
    }

    operator fun invoke(string: Image) {
        clipboard.setContents(TransferableImage(string)) { _, _ -> }
    }

}