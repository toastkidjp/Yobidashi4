package jp.toastkid.yobidashi4.domain.service.article

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ArticlesReaderService : KoinComponent {

    private val setting: Setting by inject()

    operator fun invoke(): Stream<Path> =
        Files.list(Paths.get(setting.articleFolder()))

}