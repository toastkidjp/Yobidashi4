package jp.toastkid.yobidashi4.domain.service.article

interface ArticleOpener {
    fun fromRawText(rawText: String?)
}