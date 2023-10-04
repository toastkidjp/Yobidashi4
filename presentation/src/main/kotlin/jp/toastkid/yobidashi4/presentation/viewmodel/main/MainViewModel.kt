package jp.toastkid.yobidashi4.presentation.viewmodel.main

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.WindowState
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.tab.FileTab
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.presentation.editor.finder.FindOrder
import jp.toastkid.yobidashi4.presentation.editor.legacy.MenuCommand
import kotlinx.coroutines.flow.Flow

interface MainViewModel {
    val selected: State<Int>
    val tabs: SnapshotStateList<Tab>
    fun showWebSearch(): Boolean
    fun showAggregationBox(): Boolean
    fun switchAggregationBox(newState: Boolean)
    fun initialAggregationType(): Int
    fun setInitialAggregationType(ordinal: Int)

    fun showInputBox(): Boolean
    fun setShowInputBox(action: ((String) -> Unit)? = null)
    fun invokeInputAction(input: String?)
    fun currentTab(): Tab?
    fun darkMode(): Boolean

    fun switchDarkMode()
    fun backgroundImage(): ImageBitmap
    fun loadBackgroundImage()
    fun setSelectedIndex(newIndex: Int)
    fun openTab(tab: Tab)
    fun openFileListTab(title: String, items: Collection<Path>, closeable: Boolean, type: FileTab.Type)
    fun openFile(path: Path, onBackground: Boolean = false)
    fun openPreview(path: Path, onBackground: Boolean = false)
    fun openUrl(url: String, background: Boolean)

    fun edit(path: Path, onBackground: Boolean = false)

    fun removeTabAt(index: Int)
    fun closeCurrent()
    fun addNewArticle(path: Path)
    fun saveCurrentEditorTab()
    fun updateEditorContent(path: Path, text: String, caretPosition: Int = -1, scroll: Double = -1.0, resetEditing: Boolean)
    fun openingEditor(): Boolean
    fun setShowWebSearch(newState: Boolean = true)
    fun showingSnackbar(): Boolean
    fun updateWebTab(id: String, title: String, url: String?)
    fun windowState(): WindowState
    fun toggleFullscreen()
    fun toggleFullscreenLabel(): String
    fun toggleNarrowWindow()
    fun snackbarHostState(): SnackbarHostState
    fun showSnackbar(message: String, actionLabel: String? = null, action: () -> Unit = {})

    fun articles(): List<Path>
    fun reloadAllArticle()
    fun openArticleList(): Boolean
    fun switchArticleList()
    fun hideArticleList()
    fun openTextFile(path: Path)

    fun openMemoryUsageBox(): Boolean

    fun switchMemoryUsageBox()

    fun openFind(): Boolean

    fun switchFind()

    fun inputValue(): TextFieldValue

    fun replaceInputValue(): TextFieldValue

    fun finderFlow(): Flow<FindOrder>

    fun onFindInputChange(value: TextFieldValue)
    fun onReplaceInputChange(value: TextFieldValue)

    fun findUp()

    fun findDown()

    fun replaceAll()

    fun setFindStatus(status: String)

    fun findStatus(): String

    fun droppedPathFlow(): Flow<Path>

    fun emitDroppedPath(paths: Collection<Path>)

    fun slideshowPath(): Path?

    fun slideshow(path: Path)

    fun closeSlideshow()

}