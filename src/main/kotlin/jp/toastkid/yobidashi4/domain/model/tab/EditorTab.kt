package jp.toastkid.yobidashi4.domain.model.tab

import androidx.compose.runtime.mutableStateOf
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.presentation.editor.legacy.model.Editing
import kotlin.io.path.nameWithoutExtension

data class EditorTab(
    val path: Path
): Tab {

    private val editing: Editing = Editing()

    private val titleState = mutableStateOf(path.nameWithoutExtension)

    override fun title(): String = titleState.value

    override fun closeable(): Boolean = editing.shouldShowIndicator().not()

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
        titleState.value = "${path.nameWithoutExtension}${if (editing.shouldShowIndicator()) " *" else ""}"
    }

}