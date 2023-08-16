package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.io.IOException
import jp.toastkid.yobidashi4.domain.model.clipboard.TransferableImage
import org.slf4j.LoggerFactory

class ClipboardPutterService(private val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard) {

    operator fun invoke(string: String?) {
        clipboard.setContents(StringSelection(string)) { _, _ -> }
    }

    operator fun invoke(image: Image) {
        try {
            clipboard.setContents(TransferableImage(image)) { _, _ -> }
        } catch (e: IOException) {
            LoggerFactory.getLogger(javaClass).debug("IO Exception", e)
        }
    }

}