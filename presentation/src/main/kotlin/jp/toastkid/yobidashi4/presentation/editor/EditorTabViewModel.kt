package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.runtime.mutableStateOf
import java.util.concurrent.atomic.AtomicReference
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab

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

    fun updatePreview() {
        showPreview.value = tabHolder.get().showPreview()
    }

    private fun setTab(tab: EditorTab) {
        tabHolder.set(tab)
    }

    suspend fun launch(tab: EditorTab) {
        setTab(tab)
        tab.update().collect {
            updatePreview()
        }
    }

}