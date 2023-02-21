package jp.toastkid.yobidashi4.presentation.editor.legacy

import java.awt.BorderLayout
import java.nio.file.Path
import javax.swing.JLabel
import javax.swing.JPanel
import jp.toastkid.yobidashi4.presentation.editor.legacy.service.CommandReceiverService
import jp.toastkid.yobidashi4.presentation.editor.legacy.view.EditorAreaView
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.extension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EditorFrame(
    private val statusLabel: JLabel = JLabel()
) {

    private val editorAreaView: EditorAreaView

    private var path: Path? = null

    private val panel = JPanel()

    fun getContent() = panel

    init {
        panel.layout = BorderLayout()

        val channel = Channel<MenuCommand>()
        val messageChannel = Channel<String>()
        editorAreaView = EditorAreaView(channel = channel, messageChannel = messageChannel)

        panel.add(editorAreaView.view(), BorderLayout.CENTER)

        val footer = JPanel(BorderLayout())
        statusLabel.font = statusLabel.font.deriveFont(16f)
        footer.add(statusLabel, BorderLayout.EAST)
        panel.add(footer, BorderLayout.SOUTH)

        //val finderView = FinderAreaView(finderChannel, messageChannel)

        CoroutineScope(Dispatchers.Default).launch {
            object : KoinComponent { val vm: MainViewModel by inject() }.vm.finderFlow().collect {
                editorAreaView.find(it)
            }
        }

        editorAreaView.receiveStatus {
            setStatus("Character: $it")
        }

        val commandReceiverService = CommandReceiverService(
            channel,
            editorAreaView,
            { path }
        ) { }
        CoroutineScope(Dispatchers.Default).launch {
            commandReceiverService()
        }
    }

    fun setText(path: Path, text: String) {
        this.path = path
        editorAreaView.setStyle(path.extension)
        editorAreaView.setText(text)
        setStatus("Character: ${text.length}")
    }

    private fun setStatus(status: String) {
        statusLabel.text = "${ if (editorAreaView.isEditable().not()) "Not editable " else "" } $status"
        path?.let {
            object : KoinComponent { val vm: MainViewModel by inject() }.vm.updateEditorContent(it, currentText(), -1, false)
        }
    }

    fun save() {
        editorAreaView.save()
    }

    fun setCaretPosition(position: Int) {
        editorAreaView.setCaretPosition(position)
    }

    fun caretPosition(): Int {
        return editorAreaView.caretPosition()
    }

    fun currentText() = editorAreaView.getText()

}