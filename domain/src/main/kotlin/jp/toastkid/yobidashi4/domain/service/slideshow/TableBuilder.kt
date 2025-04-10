package jp.toastkid.yobidashi4.domain.service.slideshow

import jp.toastkid.yobidashi4.domain.model.slideshow.data.TableLine
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class TableBuilder {

    private val table = mutableListOf<List<Any>>()

    private val active = AtomicBoolean(false)

    private val columnNames = AtomicReference<List<Any>?>(null)

    fun hasColumns(): Boolean = columnNames.get() != null

    fun setColumns(line: String) {
        table.clear()
        columnNames.set(line.split("|").filter(CharSequence::isNotEmpty))
    }

    fun addTableLines(line: String) {
        line.split("|").drop(1).also {
            table.add(it)
        }
    }

    fun build() = TableLine(columnNames.get() ?: emptyList(), table.toList())

    fun active() = active.get()

    fun setActive() {
        active.set(true)
    }

    fun setInactive() {
        active.set(false)
    }

    fun clear() {
        table.clear()
        columnNames.set(null)
    }

    companion object {

        fun isTableStart(line: String) = line.startsWith("|")

        fun shouldIgnoreLine(line: String) = line.startsWith("|:---")

    }

}