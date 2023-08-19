package jp.toastkid.yobidashi4.domain.service.slideshow

// TODO Implement.
class TableBuilder {


    fun hasColumns(): Boolean = false //TODO table != null

    fun setColumns(line: String) {
        val columnNames = line.split("|").drop(1).toTypedArray()
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
            //(table?.model as? DefaultTableModel)?.addRow(it.toTypedArray())
        }
    }

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