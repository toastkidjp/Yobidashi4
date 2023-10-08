package jp.toastkid.yobidashi4.domain.service.web

interface WebIconLoaderService {
    operator fun invoke(htmlSource: String, browserUrl: String?)

}