package jp.toastkid.yobidashi4.domain.service.web

import java.net.URL
import java.nio.file.Path

interface WebIconLoaderService {
    operator fun invoke(htmlSource: String, browserUrl: String?)
    fun download(
        it: String,
        faviconFolder: Path,
        targetUrl: URL
    )
}