package jp.toastkid.yobidashi4.presentation.main.content.sort

import androidx.compose.runtime.mutableStateListOf
import jp.toastkid.yobidashi4.domain.model.aggregation.StocksAggregationResult
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TableSorterTest {

    private lateinit var subject: TableSorter

    @BeforeEach
    fun setUp() {
        subject = TableSorter()
    }

    @Test
    fun invoke() {
        val result = StocksAggregationResult()
        result.put("2023-12-25", 1200, 200, 9.5)
        result.put("2023-12-26", 2000, 280, 12.2)
        result.put("2023-12-27", 1800, 180, 1.2)
        val articleStates = mutableStateListOf<Array<Any>>()
        articleStates.addAll(result.itemArrays())

        (0 .. 3).forEach {
            subject.invoke(false, result, it, articleStates)
            subject.invoke(false, result, it, articleStates)
            subject.invoke(false, result, it, articleStates)
            subject.invoke(true, result, it, articleStates)
            subject.invoke(true, result, it, articleStates)
            subject.invoke(true, result, it, articleStates)
        }
    }
}