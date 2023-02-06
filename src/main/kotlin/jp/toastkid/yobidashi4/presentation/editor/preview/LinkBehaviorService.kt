package jp.toastkid.yobidashi4.presentation.editor.preview

import jp.toastkid.yobidashi4.domain.model.article.Article
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * @author toastkidjp
 */
class LinkBehaviorService(
    private val exists: (String) -> Boolean = { true },
    private val internalLinkScheme: InternalLinkScheme = InternalLinkScheme(),
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : KoinComponent {

    private val viewModel: MainViewModel by inject()

    operator fun invoke(url: String?) {
        if (url.isNullOrBlank()) {
            return
        }

        if (!internalLinkScheme.isInternalLink(url)) {
            viewModel.openUrl(url, false)
            return
        }

        val title = internalLinkScheme.extract(url)
        CoroutineScope(mainDispatcher).launch {
            val exists = withContext(ioDispatcher) { exists(title) }
            if (exists) {
                viewModel.openFile(Article.withTitle(title).path())
            } else {
                viewModel.showSnackbar("\"$title\" does not exist.")
            }
        }
    }
}