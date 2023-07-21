package jp.toastkid.yobidashi4.domain.model.aggregation

interface AggregationResult {

    fun header(): Array<Any>

    fun itemArrays(): Collection<Array<Any>>

    fun columnClass(columnIndex: Int) = when (columnIndex) {
        itemArrays().size - 1 -> Integer::class.java
        else -> String::class.java
    }

    fun title(): String

    fun isEmpty(): Boolean

}