package jp.toastkid.yobidashi4.infrastructure.service.article

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class ArticlesReaderServiceImplementation : KoinComponent, ArticlesReaderService {

    private val setting: Setting by inject()

    override operator fun invoke(): Stream<Path> =
        Files.list(setting.articleFolderPath())

}