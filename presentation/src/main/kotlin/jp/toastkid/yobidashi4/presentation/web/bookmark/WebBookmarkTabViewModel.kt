package jp.toastkid.yobidashi4.presentation.web.bookmark

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import jp.toastkid.yobidashi4.presentation.lib.KeyboardScrollAction
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WebBookmarkTabViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val repository: BookmarkRepository by inject()

    private val bookmarks = mutableStateListOf<Bookmark>()

    private val state = LazyListState()

    private val focusRequester = FocusRequester()

    private val scrollAction = KeyboardScrollAction(state)

    private val faviconFolder = WebIcon()

    fun bookmarks() = bookmarks

    fun listState() = state

    fun focusRequester() = focusRequester

    fun scrollAction(coroutineScope: CoroutineScope, key: Key, controlDown: Boolean) =
        scrollAction.invoke(coroutineScope, key, controlDown)

    fun launch(coroutineScope: CoroutineScope, scrollPosition: Int) {
        repository.list().forEach { bookmarks.add(it) }
        focusRequester().requestFocus()
        coroutineScope.launch {
            state.scrollToItem(scrollPosition)
        }

        faviconFolder.makeFolderIfNeed()
    }

    fun delete(bookmark: Bookmark) {
        repository.delete(bookmark)
        bookmarks.remove(bookmark)
    }

    fun openUrl(url: String, onBackground: Boolean) {
        viewModel.openUrl(url, onBackground)
    }

    fun browseUri(url: String) {
        viewModel.browseUri(url)
    }

    private val currentDropdownItem = mutableStateOf<Bookmark?>(null)

    fun openingDropdown(item: Bookmark) = currentDropdownItem.value == item

    fun openDropdown(item: Bookmark) {
        currentDropdownItem.value = item
    }

    fun closeDropdown() {
        currentDropdownItem.value = null
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun onPointerEvent(pointerEvent: PointerEvent, bookmark: Bookmark) {
        if (pointerEvent.type == PointerEventType.Press
            && openingDropdown(bookmark).not()
            && pointerEvent.button == PointerButton.Secondary
        ) {
            openDropdown(bookmark)
        }
    }

    fun update(tab: WebBookmarkTab) {
        viewModel.updateScrollableTab(tab, state.firstVisibleItemIndex)
    }

    fun findFaviconPath(url: String): Path? {
        return faviconFolder.find(url)
    }

}