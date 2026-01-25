package jp.toastkid.yobidashi4.domain.model.markdown

import jp.toastkid.yobidashi4.domain.model.slideshow.data.Line

data class Markdown(
    private val title: String,
    private val lines: MutableList<Line> = mutableListOf(),
    private val subheadings: MutableList<TextBlock> = mutableListOf(),
) {

    fun title() = title

    fun add(line: Line) {
        lines.add(line)

        if (line is TextBlock && line.level != -1) {
            this.subheadings.add(line)
        }
    }

    fun addAll(lines: List<Line>) {
        this.lines.addAll(lines)
    }

    fun lines(): List<Line> = lines

    fun subheadings() = subheadings

}