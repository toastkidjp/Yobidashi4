package jp.toastkid.yobidashi4.domain.model.tab

import jp.toastkid.yobidashi4.domain.model.markdown.Markdown
import jp.toastkid.yobidashi4.domain.service.markdown.MarkdownParser
import java.nio.file.Path

data class MarkdownPreviewTab(
    private val path: Path,
    private val markdown: Markdown,
    private val scrollPosition: Int = 0
) : ScrollableContentTab, WithFilePath {

    override fun title(): String {
        return markdown.title()
    }

    override fun scrollPosition() = scrollPosition

    override fun withNewPosition(scrollPosition: Int): ScrollableContentTab {
        return MarkdownPreviewTab(this.path, this.markdown, scrollPosition)
    }

    fun markdown() = markdown

    fun slideshowSourcePath() = path

    override fun filePath(): Path = path

    companion object {

        fun with(path: Path) = MarkdownPreviewTab(
            path,
            MarkdownParser().invoke(path)
        )

    }

}