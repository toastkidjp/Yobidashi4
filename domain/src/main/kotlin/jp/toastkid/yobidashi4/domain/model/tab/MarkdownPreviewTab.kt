package jp.toastkid.yobidashi4.domain.model.tab

import jp.toastkid.yobidashi4.domain.model.markdown.Markdown

data class MarkdownPreviewTab(
    private val markdown: Markdown
) : Tab {

    private var scrollPosition = 0

    override fun title(): String {
        return markdown.title()
    }

    override fun iconPath(): String? {
        return "images/icon/ic_markdown.xml"
    }

    fun setScrollPosition(scrollPosition: Int) {
        this.scrollPosition = scrollPosition
    }

    fun scrollPosition() = scrollPosition

    fun markdown() = markdown

}