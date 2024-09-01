package jp.toastkid.yobidashi4.domain.service.photo.gif

import java.nio.file.Path

interface GifDivider {

    suspend operator fun invoke(path: Path)

}