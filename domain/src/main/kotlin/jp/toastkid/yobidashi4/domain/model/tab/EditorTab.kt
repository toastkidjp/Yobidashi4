package jp.toastkid.yobidashi4.domain.model.tab

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicReference
import kotlin.io.path.nameWithoutExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class EditorTab(val path: Path): Tab {

    private val editing: Editing = Editing()

    override fun title(): String = "${path.nameWithoutExtension}${if (editing.shouldShowIndicator()) " * " else ""}"

    override fun closeable(): Boolean = editing.shouldShowIndicator().not()

    override fun iconPath(): String {
        return "images/icon/ic_edit.xml"
    }

    private val caretPosition = AtomicReference(0)

    fun caretPosition(): Int = caretPosition.get()

    fun setCaretPosition(newPosition: Int) {
        caretPosition.set(newPosition)
    }

    private var scroll = 0.0

    fun scroll() = scroll

    fun setScroll(newPosition: Double) {
        scroll = newPosition
    }

    private val preview: AtomicReference<Preview> = AtomicReference(Preview.CLOSE)

    private val content: AtomicReference<CharSequence> = AtomicReference("")

    fun getContent(): CharSequence = content.get()

    fun setContent(newContent: CharSequence?, resetEditing: Boolean) {
        editing.setCurrentSize((newContent ?: content.get()).length)
        if (resetEditing) {
            editing.clear()
        }

        if (newContent.isNullOrBlank()) {
            return
        }
        content.set(newContent)

        CoroutineScope(Dispatchers.IO).launch {
            _updateFlow.emit(System.currentTimeMillis())
        }
    }

    fun switchPreview() {
        preview.set(if (preview.get() == Preview.HALF) Preview.CLOSE else Preview.HALF)
        CoroutineScope(Dispatchers.IO).launch {
            _updateFlow.emit(System.currentTimeMillis())
        }
    }

    fun showPreview() = preview.get() == Preview.HALF

    private val _updateFlow = MutableSharedFlow<Long>()

    override fun update(): Flow<Long> {
        return _updateFlow.asSharedFlow()
    }

    fun loadContent() {
        content.set(Files.readString(path))
    }

    private var editable = true

    fun editable() = editable

    fun switchEditable() {
        editable = !editable
    }

    enum class Preview {
        HALF, CLOSE
    }

}