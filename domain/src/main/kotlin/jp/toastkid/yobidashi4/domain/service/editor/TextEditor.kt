package jp.toastkid.yobidashi4.domain.service.editor

import java.nio.file.Path
import javax.swing.JComponent

interface TextEditor {

    fun getContent(): JComponent

    fun setText(path: Path, text: String)

    fun save()

    fun setCaretPosition(position: Int)

    fun caretPosition(): Int

    fun currentText(): String?

    fun statusLabel(): String?

    fun cancel()

}