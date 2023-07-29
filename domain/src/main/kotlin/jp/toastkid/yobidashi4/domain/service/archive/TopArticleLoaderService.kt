package jp.toastkid.yobidashi4.domain.service.archive

import java.nio.file.Path

interface TopArticleLoaderService {
    operator fun invoke(): List<Path>
}