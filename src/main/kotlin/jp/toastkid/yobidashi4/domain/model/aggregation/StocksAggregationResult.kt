package jp.toastkid.yobidashi4.domain.model.aggregation

class StocksAggregationResult : AggregationResult {

    private val dateAndSteps = mutableMapOf<String, Triple<Int, Int, Double>>()

    fun put(date: String, valuation: Int, gainOrLoss: Int, percent: Double) {
        dateAndSteps.put(date, Triple(valuation, gainOrLoss, percent))
    }

    override fun header(): Array<Any> {
        return arrayOf("Date", "Valuation", "Gain / Loss", "Percent")
    }

    override fun itemArrays(): Collection<Array<Any>> {
        return dateAndSteps.map { arrayOf(it.key, it.value.first, it.value.second, it.value.third) }
    }

    override fun resultTitleSuffix(): String {
        return "資産運用成績"
    }

    override fun isEmpty(): Boolean {
        return dateAndSteps.isEmpty()
    }

    override fun columnClass(columnIndex: Int): Class<out Any> {
        return when (columnIndex) {
            1 -> Integer::class.java
            2 -> Integer::class.java
            3 -> Double::class.java
            else -> String::class.java
        }
    }

}