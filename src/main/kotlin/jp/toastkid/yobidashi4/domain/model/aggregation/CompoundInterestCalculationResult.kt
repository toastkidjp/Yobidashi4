package jp.toastkid.yobidashi4.domain.model.aggregation

class CompoundInterestCalculationResult : AggregationResult {

    private val items = mutableListOf<Triple<Int, Int, Int>>()

    override fun header(): Array<Any> {
        return arrayOf("Year", "Single", "Compound")
    }

    override fun itemArrays(): Collection<Array<Any>> {
        return items.map { arrayOf<Any>(it.first, it.second, it.third) }
    }

    override fun columnClass(columnIndex: Int): Class<out Any> {
        return Integer::class.java
    }

    override fun resultTitleSuffix(): String {
        return " compound interests"
    }

    override fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    fun get(year: Int): Triple<Int, Int, Int>? {
        return items.firstOrNull { it.first == year }
    }

    fun put(year: Int, singleCase: Int, compoundCase: Int) {
        items.add(Triple(year, singleCase, compoundCase))
    }

}