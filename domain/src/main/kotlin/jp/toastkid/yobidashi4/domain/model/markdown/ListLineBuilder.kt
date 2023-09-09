package jp.toastkid.yobidashi4.domain.model.markdown

class ListLineBuilder {

    private val list: MutableList<String> = mutableListOf()

    private var ordered: Boolean = false

    private var taskList: Boolean = false

    fun clear() {
        list.clear()
        ordered = false
        taskList = false
    }

    fun add(item: String) {
        list.add(item.substring(item.indexOf(" ") + 1))
    }

    fun setOrdered() {
        ordered = true
    }

    fun setTaskList() {
        taskList = true
    }

    fun isNotEmpty() = list.isNotEmpty()

    fun build() = ListLine(list.toList(), ordered, taskList)

}