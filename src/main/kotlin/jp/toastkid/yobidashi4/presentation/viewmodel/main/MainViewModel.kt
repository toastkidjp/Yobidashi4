package jp.toastkid.yobidashi4.presentation.viewmodel.main

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.window.WindowState
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.tab.FileTab
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface MainViewModel {
    val selected: State<Int>
    val tabs: SnapshotStateList<Tab>
    fun showWebSearch(): Boolean

    fun currentTab(): Tab?
    fun darkMode(): Boolean

    fun switchDarkMode()
    fun backgroundImage(): ImageBitmap
    fun loadBackgroundImage()
    fun setSelectedIndex(newIndex: Int)
    fun openTab(tab: Tab)
    fun openFileListTab(title: String, items: Collection<Path>, closeable: Boolean, type: FileTab.Type)
    fun openAggregationResultTab(title: String, result: AggregationResult)
    fun openFile(path: Path, onBackground: Boolean = false)
    fun openUrl(url: String, background: Boolean)
    fun removeTabAt(index: Int)
    fun closeCurrent()
    fun addNewArticle(path: Path)
    fun updateEditorContent(path: Path, text: String, caretPosition: Int = -1, resetEditing: Boolean)
    fun openingEditor(): Boolean
    fun setShowWebSearch(newState: Boolean = true)
    fun updateWebTab(id: String, title: String, url: String?)
    fun windowState(): WindowState
    fun toggleFullscreen()
    fun toggleFullscreenLabel(): String
    fun snackbarHostState(): SnackbarHostState
    fun showSnackbar(message: String, actionLabel: String? = null, action: () -> Unit = {})

    fun articles(): List<Path>
    fun reloadAllArticle()
    fun openArticleList(): Boolean
    fun switchArticleList()
    fun openTextFile(path: Path)

    /**
     * Temporary implementation.
     */
    companion object : KoinComponent {

        private val instance: MainViewModel by inject()

        fun get() = instance

    }

}