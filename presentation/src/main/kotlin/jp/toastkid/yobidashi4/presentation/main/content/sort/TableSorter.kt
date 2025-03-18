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
            sortBy(index, articleStates, aggregationResult.columnClass(index))
        else
            sortByDescending(index, articleStates, aggregationResult.columnClass(index))

        articleStates.clear()
        articleStates.addAll(swap)
    }

    private fun sortBy(
        index: Int,
        articleStates: SnapshotStateList<Array<Any>>,
        anyClass: Class<out Any>
    ) = when (anyClass) {
        Int::class.java -> {
            articleStates.sortedBy { it[index].toString().toInt() }
        }
        Double::class.java -> {
            articleStates.sortedBy { it[index].toString().toDouble() }
        }
        else -> {
            articleStates.sortedBy { it[index].toString() }
        }
    }

    private fun sortByDescending(
        index: Int,
        articleStates: SnapshotStateList<Array<Any>>,
        anyClass: Class<out Any>
    ) = if (anyClass == Int::class.java) {
        articleStates.sortedByDescending { it[index].toString().toInt() }
    } else if (anyClass == Double::class.java) {
        articleStates.sortedByDescending { it[index].toString().toDouble() }
    } else {
        articleStates.sortedByDescending { it[index].toString() }
    }

}