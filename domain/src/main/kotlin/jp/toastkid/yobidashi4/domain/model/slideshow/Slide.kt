package jp.toastkid.yobidashi4.domain.model.slideshow

import jp.toastkid.yobidashi4.domain.model.slideshow.data.ImageLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.Line
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TextLine

class Slide {

    private var title = ""

    private var backgroundPath = ""

    private var front = false

    private val lines = mutableListOf<Line>()

    fun setTitle(title: String) {
        this.title = title
    }

    fun hasTitle() = this.title.isNotBlank()

    fun title() = this.title

    fun addText(line: String) {
        this.lines.add(TextLine(line))
    }

    fun addQuotedLines(line: String) {
        this.lines.add(TextLine("<html>$line</html>"))
    }

    fun setBackground(background: String) {
        backgroundPath = background
    }

    fun background() = backgroundPath

    fun setFront(front: Boolean) {
        this.front = front
    }

    fun isFront() = this.front

    fun addLines(lines: List<Line>) {
        this.lines.addAll(lines)
    }

    fun addLine(line: Line) {
        this.lines.add(line)
    }

    fun lines() = lines

    fun extractImageUrls(): Set<String> {
        val imageUrls = mutableSetOf<String>()
        if (backgroundPath.isNotBlank()) {
            imageUrls.add(backgroundPath)
        }
        lines.filterIsInstance<ImageLine>().map { it.source }.forEach { imageUrls.add(it) }
        return imageUrls
    }

}