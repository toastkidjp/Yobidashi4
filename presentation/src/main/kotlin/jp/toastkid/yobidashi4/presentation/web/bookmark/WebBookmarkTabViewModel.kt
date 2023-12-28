package jp.toastkid.yobidashi4.presentation.web.bookmark

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import jp.toastkid.yobidashi4.presentation.lib.KeyboardScrollAction
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WebBookmarkTabViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val repository: BookmarkRepository by inject()

    private val bookmarks = mutableStateListOf<Bookmark>()

    private val state = LazyListState()

    private val focusRequester = FocusRequester()

    private val scrollAction = KeyboardScrollAction(state)

    fun bookmarks() = bookmarks

    fun listState() = state

    fun focusRequester() = focusRequester

    fun scrollAction(coroutineScope: CoroutineScope, key: Key, controlDown: Boolean) =
        scrollAction.invoke(coroutineScope, key, controlDown)

    fun launch() {
        repository.list().forEach { bookmarks.add(it) }
        focusRequester().requestFocus()
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

}