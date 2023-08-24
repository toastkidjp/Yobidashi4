package jp.toastkid.yobidashi4.domain.service.slideshow

import jp.toastkid.yobidashi4.domain.model.slideshow.data.TableLine

class TableBuilder {

    private val table = mutableListOf<List<Any>>()

    private var columnNames: List<Any>? = null

    fun hasColumns(): Boolean = columnNames != null

    fun setColumns(line: String) {
        table.clear()
        columnNames = line.split("|").drop(1)
    }

    fun addTableLines(line: String) {
        line.split("|").drop(1).also {
            table.add(it)
        }
    }

    fun build() = TableLine(columnNames ?: emptyList(), table)

    companion object {

        fun isTableStart(line: String) = line.startsWith("|")

        fun shouldIgnoreLine(line: String) = line.startsWith("|:---")

    }

}