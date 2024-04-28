package jp.toastkid.yobidashi4.domain.service.article.finder

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface AsynchronousArticleIndexerService {

    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO)

}