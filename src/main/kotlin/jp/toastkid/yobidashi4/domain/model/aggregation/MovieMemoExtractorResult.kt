package jp.toastkid.yobidashi4.domain.model.aggregation

class MovieMemoExtractorResult : AggregationResult {

    private val items = mutableListOf<MovieMemo>()

    override fun header(): Array<Any> {
        return MovieMemo.header()
    }

    override fun itemArrays(): Collection<Array<Any>> {
        return items.map { it.toArray() }
    }

    override fun title(): String {
        return String.format("Movies: %,d", items.size)
    }

    override fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    fun add(date: String, title: String) {
        items.add(MovieMemo(date, title))
    }

}