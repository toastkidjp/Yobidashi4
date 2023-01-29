package jp.toastkid.yobidashi4.domain.model.aggregation

class FindResult(private val keyword: String) : AggregationResult {

    private val items = mutableMapOf<String, List<String>>()

    override fun header(): Array<Any> {
        return arrayOf("Name", "Lines")
    }

    override fun itemArrays(): Collection<Array<Any>> {
        return items.map { arrayOf<Any>(it.key, it.value) }
    }

    override fun resultTitleSuffix(): String {
        return "'$keyword' find result ${items.size}"
    }

    override fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    fun add(fileName: String, filteredList: List<String>) {
        items.put(fileName, filteredList)
    }
}