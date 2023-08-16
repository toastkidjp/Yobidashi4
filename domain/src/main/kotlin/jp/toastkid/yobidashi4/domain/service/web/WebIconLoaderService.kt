package jp.toastkid.yobidashi4.domain.service.web

import java.nio.file.Path

interface WebIconLoaderService {
    operator fun invoke(htmlSource: String, browserUrl: String?)
    fun download(
        iconUrl: String,
        faviconFolder: Path,
        targetHost: String?
    )
}