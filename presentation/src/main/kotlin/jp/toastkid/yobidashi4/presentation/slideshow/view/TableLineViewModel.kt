package jp.toastkid.yobidashi4.presentation.slideshow.view

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class TableLineViewModel {

    private var lastSorted = -1 to false

    private val tableData: MutableState<List<List<Any>>> =  mutableStateOf(emptyList())

    private val headerCursorOn = mutableStateOf(false)

    fun tableData() = tableData.value

    fun start(table: List<List<Any>>) {
        tableData.value = table
    }

    private fun sort(
        lastSortOrder: Boolean,
        index: Int,
        articleStates: MutableState<List<List<Any>>>
    ) {
        val first = articleStates.value.firstOrNull() ?: return
        val snapshot = articleStates.value
        val swap = if (lastSortOrder)
            if (first[index].toString().toDoubleOrNull() != null) {
                snapshot.sortedBy { it[index].toString().toDoubleOrNull() ?: 0.0 }
            } else if (first[index].toString().toIntOrNull() != null) {
                snapshot.sortedBy { it[index].toString().toIntOrNull() ?: 0 }
            } else {
                snapshot.sortedBy { it[index].toString() }
            }
        else
            if (first[index].toString().toDoubleOrNull() != null) {
                snapshot.sortedByDescending { it[index].toString().toDoubleOrNull() ?: 0.0 }
            } else if (first[index].toString().toIntOrNull() != null) {
                snapshot.sortedByDescending { it[index].toString().toIntOrNull() ?: 0 }
            } else {
                snapshot.sortedByDescending { it[index].toString() }
            }

        articleStates.value = swap
    }

    fun clickHeaderColumn(index: Int) {
        val lastSortOrder = if (lastSorted.first == index) lastSorted.second else false
        lastSorted = index to lastSortOrder.not()

        sort(lastSortOrder, index, tableData)
    }

    fun setCursorOnHeader() {
        headerCursorOn.value = true
    }

    fun setCursorOffHeader() {
        headerCursorOn.value = false
    }

    fun onCursorOnHeader() = headerCursorOn.value

}