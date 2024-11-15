package jp.toastkid.yobidashi4.domain.model.slideshow

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import jp.toastkid.yobidashi4.domain.model.slideshow.data.ImageLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.Line
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TextLine

class Slide {

    private val title = AtomicReference("")

    private val backgroundPath = AtomicReference("")

    private val front = AtomicBoolean(false)

    private val lines = mutableListOf<Line>()

    fun setTitle(title: String) {
        this.title.set(title)
    }

    fun hasTitle() = this.title.get().isNotBlank()

    fun title(): String = this.title.get()

    fun addText(line: String) {
        this.lines.add(TextLine(line))
    }

    fun addQuotedLines(line: String) {
        this.lines.add(TextLine("<html>$line</html>"))
    }

    fun setBackground(background: String) {
        backgroundPath.set(background)
    }

    fun background(): String = backgroundPath.get()

    fun setFront(front: Boolean) {
        this.front.set(front)
    }

    fun isFront() = this.front.get()

    fun addLines(lines: List<Line>) {
        this.lines.addAll(lines)
    }

    fun addLine(line: Line) {
        this.lines.add(line)
    }

    fun lines() = lines

    fun extractImageUrls(): Set<String> {
        val imageUrls = mutableSetOf<String>()
        val backgroundPath = this.backgroundPath.get()
        if (backgroundPath.isNotBlank()) {
            imageUrls.add(backgroundPath)
        }
        lines.filterIsInstance<ImageLine>().map { it.source }.forEach { imageUrls.add(it) }
        return imageUrls
    }

}