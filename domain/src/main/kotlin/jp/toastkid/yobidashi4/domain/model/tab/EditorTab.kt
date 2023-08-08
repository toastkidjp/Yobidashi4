package jp.toastkid.yobidashi4.domain.model.tab

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class EditorTab(
    val path: Path
): Tab {

    private var title: String = path.nameWithoutExtension

    private val editing: Editing = Editing()

    override fun title(): String = "${path.nameWithoutExtension}${if (editing.shouldShowIndicator()) " * " else ""}"

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
        val currentEditing = editing.shouldShowIndicator()
        editing.setCurrentSize((newContent ?: content).length)
        if (resetEditing) {
            editing.clear()
        }

        if (newContent.isNullOrBlank()) {
            return
        }
        content = newContent

        if (currentEditing == editing.shouldShowIndicator()) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            _updateFlow.emit(System.currentTimeMillis())
        }
    }

    private var preview = false

    fun switchPreview() {
        preview = preview.not()
    }

    fun showPreview() = preview

    private val _updateFlow = MutableSharedFlow<Long>()

    override fun update(): Flow<Long> {
        return _updateFlow.asSharedFlow()
    }

}