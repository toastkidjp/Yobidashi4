package jp.toastkid.yobidashi4.domain.model.markdown

import java.util.concurrent.atomic.AtomicBoolean

class ListLineBuilder {

    private val list: MutableList<String> = mutableListOf()

    private val ordered = AtomicBoolean(false)

    private val taskList = AtomicBoolean(false)

    fun clear() {
        list.clear()
        ordered.set(false)
        taskList.set(false)
    }

    fun add(item: String) {
        list.add(item.substring(item.indexOf(" ") + 1))
    }

    fun setOrdered() {
        ordered.set(true)
    }

    fun setTaskList() {
        taskList.set(true)
    }

    fun isNotEmpty() = list.isNotEmpty()

    fun build() = ListLine(list.toList(), ordered.get(), taskList.get())

}