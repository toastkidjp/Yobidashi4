package jp.toastkid.yobidashi4.domain.model.aggregation

class ArticleLengthAggregationResult : AggregationResult {

    private val map: MutableMap<String, Long> = hashMapOf()

    fun put(key: String, value: Long) {
        map[key] = value
    }

    private fun sum() = map.values.sum()

    override fun header(): Array<Any> =
            arrayOf("Title", "Length")

    override fun itemArrays(): Collection<Array<Any>> =
            map.entries.map { arrayOf(it.key, it.value) }

    override fun resultTitleSuffix(): String {
        return "Total: ${sum()}"
    }

    override fun columnClass(columnIndex: Int): Class<out Any> {
        return when (columnIndex) {
            1 -> Integer::class.java
            else -> String::class.java
        }
    }

    override fun isEmpty(): Boolean {
        return map.isEmpty()
    }

}