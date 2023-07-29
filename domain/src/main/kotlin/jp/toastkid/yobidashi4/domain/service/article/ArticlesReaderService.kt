package jp.toastkid.yobidashi4.domain.service.article

import java.nio.file.Path
import java.util.stream.Stream

interface ArticlesReaderService {
    operator fun invoke(): Stream<Path>
}