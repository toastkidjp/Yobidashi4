package jp.toastkid.yobidashi4.infrastructure.service.web

import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.cef.callback.CefContextMenuParams
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BookmarkInsertion : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    private val repository: BookmarkRepository by inject()

    operator fun invoke(params: CefContextMenuParams? = null, latestUrl: String?) {
        val url = when {
            params == null -> latestUrl
            params.linkUrl != null && params.linkUrl.isNotBlank() -> params.linkUrl
            params.sourceUrl != null && params.sourceUrl.isNotBlank() -> params.sourceUrl
            params.pageUrl != null && params.pageUrl.isNotBlank() -> params.pageUrl
            else -> latestUrl
        } ?: return

        val currentTab = mainViewModel.currentTab()
        @Suppress("IfThenToElvis")
        val title = if (currentTab != null) currentTab.title() else url

        invoke(title, url)
    }

    operator fun invoke(title: String, url: String) {
        val item = Bookmark(title, url)

        repository.add(item)

        mainViewModel.showSnackbar("Add bookmark: $item")
    }

}