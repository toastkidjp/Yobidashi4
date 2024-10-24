package jp.toastkid.yobidashi4.domain.model.markdown

import java.util.concurrent.atomic.AtomicBoolean

class ListLineBuilder {

    private val list: MutableList<String> = mutableListOf()

    private val ordered = AtomicBoolean(false)

    private var taskList: Boolean = false

    fun clear() {
        list.clear()
        ordered.set(false)
        taskList = false
    }

    fun add(item: String) {
        list.add(item.substring(item.indexOf(" ") + 1))
    }

    fun setOrdered() {
        ordered.set(true)
    }

    fun setTaskList() {
        taskList = true
    }

    fun isNotEmpty() = list.isNotEmpty()

    fun build() = ListLine(list.toList(), ordered.get(), taskList)

}