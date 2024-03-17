package jp.toastkid.yobidashi4.domain.model.aggregation

class OutgoAggregationResult(val target: String): AggregationResult {

    private val map: MutableList<Outgo> = mutableListOf()

    fun add(date: String, title: String, value: Int) {
        map.add(Outgo(date, title, value))
    }

    fun aggregate() {
        val aggregated = map.groupBy { it.date }.map { Outgo(it.key, it.key, it.value.sumOf { it.price }) }
        map.clear()
        map.addAll(aggregated)
    }

    fun sum(): Int {
        return map.map { it.price }.sum()
    }

    override fun itemArrays(): List<Array<Any>> = map.map { arrayOf(it.date, it.title, it.price) }

    override fun header(): Array<Any> = arrayOf("Date", "Item", "Price")

    override fun title(): String {
        return String.format("Total: %,d", sum())
    }

    override fun isEmpty(): Boolean {
        return map.isEmpty()
    }

    override fun columnClass(columnIndex: Int): Class<out Any> {
        return when (columnIndex) {
            2 -> Int::class.java
            else -> String::class.java
        }
    }

}