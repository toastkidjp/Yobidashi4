package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.runtime.mutableStateOf
import java.util.concurrent.atomic.AtomicReference
import jp.toastkid.yobidashi4.domain.model.markdown.Markdown
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.service.markdown.MarkdownParser

class EditorTabViewModel {

    private val tabHolder: AtomicReference<EditorTab> = AtomicReference()

    private val status = mutableStateOf("")

    fun status() = status.value

    fun updateStatus(it: String) {
        val tab = tabHolder.get() ?: return
        status.value = (if (tab.editable()) "" else "Not editable | ") + it
    }

    private val showPreview = mutableStateOf(false)

    fun showPreview() = showPreview.value

    private fun updatePreview() {
        val tab = tabHolder.get() ?: return
        showPreview.value = tab.showPreview()
        preview.value = MarkdownParser().invoke(tab.path)
    }

    private fun setTab(tab: EditorTab) {
        tabHolder.set(tab)
    }

    private val preview = mutableStateOf<Markdown>(Markdown(""))

    fun preview() = preview.value

    suspend fun launch(tab: EditorTab) {
        setTab(tab)
        tab.update().collect {
            updatePreview()
        }
    }

}