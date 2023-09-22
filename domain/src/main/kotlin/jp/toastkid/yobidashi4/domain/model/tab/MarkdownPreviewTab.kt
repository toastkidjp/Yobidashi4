package jp.toastkid.yobidashi4.domain.model.tab

import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.markdown.Markdown
import jp.toastkid.yobidashi4.domain.service.markdown.MarkdownParser

data class MarkdownPreviewTab(
    private val path: Path,
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

    fun slideshowSourcePath() = path

    companion object {
        fun with(path: Path) = MarkdownPreviewTab(
            path,
            MarkdownParser().invoke(path)
        )

    }

}