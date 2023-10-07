package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import java.nio.file.Files
import java.util.stream.Collectors
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.archive.TopArticleLoaderService
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class TopArticleLoaderServiceImplementation : KoinComponent, TopArticleLoaderService {

    private val setting: Setting by inject()

    override operator fun invoke() =
        Files.list(setting.articleFolderPath())
            .sorted { o1, o2 -> -Files.getLastModifiedTime(o1).compareTo(Files.getLastModifiedTime(o2)) }
            .collect(Collectors.toList())

}