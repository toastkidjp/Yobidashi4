package jp.toastkid.yobidashi4.presentation.viewmodel.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.TextContextMenu
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.TrayState
import androidx.compose.ui.window.WindowState
import jp.toastkid.yobidashi4.domain.model.find.FindOrder
import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import jp.toastkid.yobidashi4.domain.model.tab.CalendarTab
import jp.toastkid.yobidashi4.domain.model.tab.FileTab
import jp.toastkid.yobidashi4.domain.model.tab.ScrollableContentTab
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import kotlinx.coroutines.flow.Flow
import java.nio.file.Path

interface MainViewModel {
    val selected: State<Int>
    val tabs: List<Tab>
    fun showWebSearch(): Boolean
    fun showAggregationBox(): Boolean
    fun switchAggregationBox(newState: Boolean)
    fun initialAggregationType(): Int
    fun setInitialAggregationType(ordinal: Int)

    fun findArticle(query: String?)

    fun showInputBox(): Boolean
    fun setShowInputBox(action: ((String) -> Unit)? = null)
    fun invokeInputAction(input: String?)
    fun currentTab(): Tab?
    fun darkMode(): Boolean

    fun switchDarkMode()
    fun backgroundImage(): ImageBitmap
    fun showBackgroundImage(): Boolean
    fun switchUseBackground()
    fun loadBackgroundImage()

    fun setSelectedIndex(newIndex: Int)

    fun moveTabIndex(moveBy: Int)
    fun openTab(tab: Tab)
    fun openFileListTab(title: String, items: Collection<Path>, type: FileTab.Type)
    fun openFile(path: Path)
    fun openPreview(path: Path, onBackground: Boolean = false)
    fun openUrl(url: String, background: Boolean)

    fun webSearch(query: String?, background: Boolean = false)

    fun webSearchWithSelectedText()

    fun edit(path: Path, onBackground: Boolean = false)
    fun editWithTitle(title: String, onBackground: Boolean = false)

    fun browseUri(uri: String?)

    fun removeTabAt(index: Int)
    fun closeCurrent()
    fun closeOtherTabs()
    fun closeAllTabs()

    fun makeNewArticle()

    fun saveCurrentEditorTab()
    fun saveAllEditorTab()
    fun updateEditorContent(path: Path, text: CharSequence, caretPosition: Int = -1, scroll: Double = -1.0, resetEditing: Boolean)
    fun openingEditor(): Boolean
    fun setShowWebSearch(newState: Boolean = true)
    fun updateWebTab(id: String, title: String, url: String?)
    fun updateCalendarTab(tab: CalendarTab, year: Int, month: Int)
    fun updateScrollableTab(tab: ScrollableContentTab, scrollPosition: Int)

    fun replaceTab(target: Tab, replacement: Tab)

    fun windowState(): WindowState

    fun windowVisible(): Boolean

    fun toggleFullscreen()
    fun toggleFullscreenLabel(): String
    fun toggleNarrowWindow()

    fun toDefaultWindowSize()

    fun showingSnackbar(): Boolean
    fun snackbarHostState(): SnackbarHostState
    fun showSnackbar(message: String, actionLabel: String? = null, action: () -> Unit = {})

    fun dismissSnackbar()

    fun articles(): List<Path>
    fun reloadAllArticle()
    fun openArticleList(): Boolean
    fun switchArticleList()
    fun hideArticleList()
    fun openTextFile(path: Path)

    fun openWorldTime(): Boolean

    fun toggleWorldTime()

    fun openMemoryUsageBox(): Boolean

    fun switchMemoryUsageBox()

    fun openFind(): Boolean

    fun switchFind()

    fun inputValue(): TextFieldValue

    fun replaceInputValue(): TextFieldValue

    fun caseSensitive(): Boolean

    fun switchCaseSensitive()

    fun finderFlow(): Flow<FindOrder>

    fun onFindInputChange(value: TextFieldValue)
    fun onReplaceInputChange(value: TextFieldValue)

    fun findUp()

    fun findDown()

    fun replaceAll()

    fun setFindStatus(status: String)

    fun findStatus(): String

    fun emitDroppedPath(paths: Collection<Path>)

    fun registerDroppedPathReceiver(receiver: (Path) -> Unit)

    fun unregisterDroppedPathReceiver()

    suspend fun launchDroppedPathFlow()

    fun slideshowPath(): Path?

    fun slideshow(path: Path)

    fun closeSlideshow()

    @OptIn(ExperimentalFoundationApi::class)
    fun setTextManager(textManager: TextContextMenu.TextManager)

    fun selectedText(): String?

    fun putSecondaryClickItem(item: String)

    fun getSecondaryClickItem(): String

    fun trayState(): TrayState

    fun sendNotification(notificationEvent: NotificationEvent)

    fun openInputHistory(category: String)

}