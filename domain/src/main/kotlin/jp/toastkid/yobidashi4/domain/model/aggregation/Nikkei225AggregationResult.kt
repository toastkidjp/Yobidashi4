package jp.toastkid.yobidashi4.domain.model.aggregation

class Nikkei225AggregationResult : AggregationResult {

    private val map = mutableMapOf<String, Pair<String, String>>()

    private val cache: MutableList<Array<Any>> = mutableListOf()

    override fun header(): Array<Any> {
        return arrayOf("Date", "Price", "Diff")
    }

    override fun itemArrays(): Collection<Array<Any>> {
        if (cache.size != map.size) {
            cache.clear()
            map
                .map { arrayOf<Any>(it.key, it.value.first, it.value.second) }
                .forEach(cache::add)
        }
        return cache
    }

    override fun title(): String {
        return " Nikkei 225"
    }

    override fun isEmpty(): Boolean {
        return map.isEmpty()
    }

    fun put(first: String, count: String, diff: String) {
        map.put(first, count to diff)
    }
}