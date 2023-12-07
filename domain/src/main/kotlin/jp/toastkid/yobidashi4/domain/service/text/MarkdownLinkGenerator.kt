package jp.toastkid.yobidashi4.domain.service.text

class MarkdownLinkGenerator {

    operator fun invoke(title: String?, link: String?): String = "[${title ?: link}]($link)"

}