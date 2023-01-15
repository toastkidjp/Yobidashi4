package jp.toastkid.yobidashi4.domain.model.aggregation

class Nikkei225AggregationResult : AggregationResult {

    private val map = mutableMapOf<String, Pair<String, String>>()

    override fun header(): Array<Any> {
        return arrayOf("Date", "Price", "Diff")
    }

    override fun itemArrays(): Collection<Array<Any>> {
        return map.map { arrayOf<Any>(it.key, it.value.first, it.value.second) }
    }

    override fun resultTitleSuffix(): String {
        return " Nikkei 225"
    }

    override fun isEmpty(): Boolean {
        return map.isEmpty()
    }

    fun put(first: String, count: String, diff: String) {
        map.put(first, count to diff)
    }
}