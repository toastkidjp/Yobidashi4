package jp.toastkid.yobidashi4.presentation.markdown

class TableSortService {

    operator fun invoke(
        lastSortOrder: Boolean,
        index: Int,
        snapshot: List<List<Any>>
    ): List<List<Any>>? {
        val first = snapshot.firstOrNull() ?: return null
        val swap = if (lastSortOrder)
            if (first[index].toString().toDoubleOrNull() != null) {
                snapshot.sortedBy { it[index].toString().toDoubleOrNull() ?: 0.0 }
            } else {
                snapshot.sortedBy { it[index].toString() }
            }
        else
            if (first[index].toString().toDoubleOrNull() != null) {
                snapshot.sortedByDescending { it[index].toString().toDoubleOrNull() ?: 0.0 }
            } else {
                snapshot.sortedByDescending { it[index].toString() }
            }

        return swap
    }

}