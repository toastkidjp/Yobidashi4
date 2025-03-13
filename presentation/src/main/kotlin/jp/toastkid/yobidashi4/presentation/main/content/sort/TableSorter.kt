package jp.toastkid.yobidashi4.presentation.main.content.sort

import androidx.compose.runtime.snapshots.SnapshotStateList
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult

class TableSorter {

    operator fun invoke(
        lastSortOrder: Boolean,
        aggregationResult: AggregationResult,
        index: Int,
        articleStates: SnapshotStateList<Array<Any>>
    ) {
        val swap = if (lastSortOrder)
            if (aggregationResult.columnClass(index) == Int::class.java) {
                articleStates.sortedBy { it[index].toString().toIntOrNull() ?: 0 }
            } else if (aggregationResult.columnClass(index) == Double::class.java) {
                articleStates.sortedBy { it[index].toString().toDoubleOrNull() ?: 0.0 }
            } else {
                articleStates.sortedBy { it[index].toString() }
            }
        else
            if (aggregationResult.columnClass(index) == Int::class.java) {
                articleStates.sortedByDescending { it[index].toString().toIntOrNull() ?: 0 }
            } else if (aggregationResult.columnClass(index) == Double::class.java) {
                articleStates.sortedByDescending { it[index].toString().toDoubleOrNull() ?: 0.0 }
            } else {
                articleStates.sortedByDescending { it[index].toString() }
            }

        articleStates.clear()
        articleStates.addAll(swap)
    }

}