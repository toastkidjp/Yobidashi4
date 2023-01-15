package jp.toastkid.yobidashi4.domain.model.aggregation

class StepsAggregationResult : AggregationResult {

    private val dateAndSteps = mutableMapOf<String, Pair<Int, Int>>()

    fun put(date: String, steps: Int, kcal: Int) {
        dateAndSteps.put(date, steps to kcal)
    }

    override fun header(): Array<Any> {
        return arrayOf("Date", "Steps", "Kcal")
    }

    override fun itemArrays(): Collection<Array<Any>> {
        return dateAndSteps.map { arrayOf(it.key, it.value.first, it.value.second) }
    }

    override fun resultTitleSuffix(): String {
        return "歩数"
    }

    override fun isEmpty(): Boolean {
        return dateAndSteps.isEmpty()
    }

    override fun columnClass(columnIndex: Int): Class<out Any> {
        return when (columnIndex) {
            1 -> Integer::class.java
            else -> String::class.java
        }
    }

}