package jp.toastkid.yobidashi4.domain.model.aggregation

class CompoundInterestCalculationResult : AggregationResult {

    private val items = mutableListOf<Triple<Int, Long, Long>>()

    override fun header(): Array<Any> {
        return arrayOf("Year", "Single", "Compound")
    }

    override fun itemArrays(): Collection<Array<Any>> {
        return items.map { arrayOf<Any>(it.first, it.second, it.third) }
    }

    override fun columnClass(columnIndex: Int): Class<out Any> {
        if (columnIndex == 1 || columnIndex == 2) {
            return Long::class.java
        }
        return Int::class.java
    }

    override fun title(): String {
        return " compound interests"
    }

    override fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    fun get(year: Int): Triple<Int, Long, Long>? {
        return items.firstOrNull { it.first == year }
    }

    fun put(year: Int, singleCase: Long, compoundCase: Long) {
        items.add(Triple(year, singleCase, compoundCase))
    }

}