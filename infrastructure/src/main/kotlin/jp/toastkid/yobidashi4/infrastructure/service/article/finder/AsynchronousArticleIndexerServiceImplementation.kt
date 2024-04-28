package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.article.finder.AsynchronousArticleIndexerService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class AsynchronousArticleIndexerServiceImplementation : AsynchronousArticleIndexerService, KoinComponent {

    private val indexFolder = Path.of("temporary/finder/index")

    private val setting: Setting by inject()

    private val dataFolder = setting.articleFolderPath()

    override fun invoke(dispatcher: CoroutineDispatcher) {
        if (Files.exists(dataFolder).not()) {
            return
        }

        CoroutineScope(dispatcher).launch {
            val indexer = FullTextSearchIndexer(indexFolder)
            indexer.createIndex(dataFolder)
            indexer.close()
        }
    }

}