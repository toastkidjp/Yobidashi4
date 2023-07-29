package jp.toastkid.yobidashi4.domain.service.media

import java.nio.file.Path

interface MediaPlayerInvoker {
    operator fun invoke(mediaFilePath: Path)
}