package jp.toastkid.yobidashi4.domain.service.slideshow

import jp.toastkid.yobidashi4.domain.model.slideshow.data.TableLine

class TableBuilder {

    private val table = mutableListOf<List<Any>>()

    private var columnNames: List<Any>? = null

    fun hasColumns(): Boolean = columnNames != null

    fun setColumns(line: String) {
        table.clear()
        columnNames = line.split("|").drop(1)
        /*TODO val tableModel = DefaultTableModel(columnNames, 0)
        table = JTable(tableModel)
        val font = Font(Font.SANS_SERIF, Font.PLAIN, 40)
        table?.tableHeader?.font = font
        table?.font = font
        table?.rowHeight = 120
        table?.isFocusable = false
        table?.isEnabled = false*/
    }

    fun addTableLines(line: String) {
        line.split("|").drop(1).also {
            table.add(it)
            //(table?.model as? DefaultTableModel)?.addRow(it.toTypedArray())
        }
    }

    fun build() = TableLine(columnNames ?: emptyList(), table)

    /*fun get(): JComponent? = JPanel().also {
        it.layout = BoxLayout(it, BoxLayout.PAGE_AXIS)
        it.add(table?.tableHeader)
        it.add(table)
    }*/

    companion object {

        fun isTableStart(line: String) = line.startsWith("|")

        fun shouldIgnoreLine(line: String) = line.startsWith("|:---")

    }

}