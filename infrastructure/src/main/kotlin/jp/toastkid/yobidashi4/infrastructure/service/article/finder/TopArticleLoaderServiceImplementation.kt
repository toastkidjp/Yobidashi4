package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.archive.TopArticleLoaderService
import jp.toastkid.yobidashi4.infrastructure.extension.extension
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.file.Path

@Single
class TopArticleLoaderServiceImplementation(
    private val fileSystem: FileSystem
) : KoinComponent, TopArticleLoaderService {

    private val setting: Setting by inject()

    private val targetExtensions = setOf("txt", "md")

    override operator fun invoke(): List<Path> =
        fileSystem.list(setting.articleFolderPath().toOkioPath())
            .asSequence()
            .filter { item -> targetExtensions.contains(item.extension) }
            .map { it to fileSystem.metadata(it).lastModifiedAtMillis }
            .sortedByDescending { it.second }
            .map { it.first.toNioPath() }
            .toList()

}