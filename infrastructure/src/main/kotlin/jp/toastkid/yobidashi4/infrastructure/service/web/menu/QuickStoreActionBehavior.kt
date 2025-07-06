package jp.toastkid.yobidashi4.infrastructure.service.web.menu

import jp.toastkid.yobidashi4.domain.model.download.DownloadFolder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL
import java.nio.file.Files

class QuickStoreActionBehavior {

    operator fun invoke(sourceUrl: URL, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        @Suppress("BlockingMethodInNonBlockingContext")
        CoroutineScope(dispatcher).launch {
            val image = sourceUrl.openStream().readAllBytes() ?: return@launch

            val downloadFolder = DownloadFolder()
            downloadFolder.makeIfNeed()

            Files.write(downloadFolder.assignQuickStorePath(sourceUrl.file), image)
        }
    }
}