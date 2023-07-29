package jp.toastkid.yobidashi4.domain.model.tab

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

data class EditorTab(
    val path: Path
): Tab {

    private val editing: Editing = Editing()

    private var titleState = path.nameWithoutExtension

    override fun title(): String = titleState

    override fun closeable(): Boolean = editing.shouldShowIndicator().not()

    override fun iconPath(): String? {
        return "images/icon/ic_edit.xml"
    }

    private var caretPosition = 0

    fun caretPosition() = caretPosition

    fun setCaretPosition(newPosition: Int) {
        caretPosition = newPosition
    }

    private var content: String = Files.readString(path)

    fun getContent() = content

    fun setContent(newContent: String?, resetEditing: Boolean) {
        editing.setCurrentSize(content.length)
        if (resetEditing) {
            editing.clear()
        }
        if (newContent.isNullOrBlank()) {
            return
        }
        content = newContent
        titleState = "${path.nameWithoutExtension}${if (editing.shouldShowIndicator()) " * " else ""}"
    }

    private var preview = false

    fun switchPreview() {
        preview = preview.not()
    }

    fun showPreview() = preview

}