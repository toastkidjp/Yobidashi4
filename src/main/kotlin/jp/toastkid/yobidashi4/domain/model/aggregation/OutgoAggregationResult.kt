package jp.toastkid.yobidashi4.domain.model.aggregation

class OutgoAggregationResult(val target: String): AggregationResult {

    private val map: MutableList<Outgo> = mutableListOf()

    fun add(date: String, title: String, value: Int) {
        map.add(Outgo(date, title, value))
    }

    fun sum(): Int {
        return map.map { it.price }.sum()
    }

    override fun itemArrays(): List<Array<Any>> = map.map { arrayOf(it.date, it.title, it.price) }

    override fun header(): Array<Any> = arrayOf("Date", "Item", "Price")

    override fun resultTitleSuffix(): String {
        return String.format("Total: %,d", sum())
    }

    override fun isEmpty(): Boolean {
        return map.isEmpty()
    }

}