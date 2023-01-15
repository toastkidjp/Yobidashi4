package jp.toastkid.yobidashi4.domain.service.archive

import java.nio.file.Files
import java.util.stream.Collectors
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TopArticleLoaderService : KoinComponent {

    private val setting: Setting by inject()

    operator fun invoke() =
        Files.list(setting.articleFolderPath())
            .collect(Collectors.toList())
            .sortedByDescending { Files.getLastModifiedTime(it).toMillis() }

}