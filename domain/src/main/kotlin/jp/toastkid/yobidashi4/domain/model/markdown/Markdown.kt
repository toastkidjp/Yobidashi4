package jp.toastkid.yobidashi4.domain.model.markdown

import jp.toastkid.yobidashi4.domain.model.slideshow.data.Line

data class Markdown(
    private val title: String,
    private val lines: MutableList<Line> = mutableListOf()
) {

    fun add(line: Line) {
        lines.add(line)
    }

    fun lines(): List<Line> = lines

}