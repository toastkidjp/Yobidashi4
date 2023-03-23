package jp.toastkid.yobidashi4.infrastructure.service.editor.legacy

import androidx.compose.runtime.mutableStateOf
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.service.editor.TextEditor
import jp.toastkid.yobidashi4.presentation.editor.legacy.service.CommandReceiverService
import jp.toastkid.yobidashi4.presentation.editor.legacy.view.EditorAreaView
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.extension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class SwingTextEditor : TextEditor {

    private val editorAreaView: EditorAreaView = EditorAreaView()

    private val statusLabel = mutableStateOf("")

    private var path: Path? = null

    private var commandFlowJob: Job? = null

    override fun getContent() = editorAreaView.view()

    init {

        CoroutineScope(Dispatchers.Default).launch {
            object : KoinComponent { val vm: MainViewModel by inject() }.vm.finderFlow().collect {
                editorAreaView.find(it)
            }
        }

        editorAreaView.receiveStatus {
            setStatus("Character: $it")
        }

        val commandReceiverService = CommandReceiverService(editorAreaView)
        commandFlowJob = CoroutineScope(Dispatchers.Default).launch {
            commandReceiverService()
        }
    }

    override fun setText(path: Path, text: String) {
        this.path = path
        editorAreaView.setStyle(path.extension)
        editorAreaView.setText(text)
        setStatus("Character: ${text.length}")
    }

    private fun setStatus(status: String) {
        statusLabel.value = "${ if (editorAreaView.isEditable().not()) "Not editable " else "" } $status"
        path?.let {
            object : KoinComponent { val vm: MainViewModel by inject() }.vm.updateEditorContent(it, currentText(), -1, false)
        }
    }

    override  fun save() {
        editorAreaView.save()
    }

    override fun setCaretPosition(position: Int) {
        editorAreaView.setCaretPosition(position)
    }

    override fun caretPosition(): Int {
        return editorAreaView.caretPosition()
    }

    override fun currentText() = editorAreaView.getText() ?: ""

    override fun statusLabel() = statusLabel.value

    override fun cancel() {
        commandFlowJob?.cancel()
    }

}