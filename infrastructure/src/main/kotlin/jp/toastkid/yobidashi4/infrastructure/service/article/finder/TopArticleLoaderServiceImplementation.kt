package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.archive.TopArticleLoaderService
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.streams.asSequence

@Single
class TopArticleLoaderServiceImplementation : KoinComponent, TopArticleLoaderService {

    private val setting: Setting by inject()

    private val targetExtensions = setOf("txt", "md")

    override operator fun invoke(): List<Path> =
        Files.list(setting.articleFolderPath())
            .asSequence()
            .filter { item -> targetExtensions.contains(item.extension) }
            .map { it to Files.getLastModifiedTime(it) }
            .sortedByDescending { it.second }
            .map { it.first }
            .toList()

}