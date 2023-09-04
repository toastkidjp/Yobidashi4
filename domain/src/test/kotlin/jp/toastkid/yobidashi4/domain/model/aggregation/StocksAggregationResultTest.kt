package jp.toastkid.yobidashi4.domain.model.aggregation

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StocksAggregationResultTest {

    @InjectMockKs
    private lateinit var stocksAggregationResult: StocksAggregationResult

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        stocksAggregationResult.put("2023-08-08(Tue)", 200000, 3, 2.1)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun header() {
        assertEquals("Date, Valuation, Gain / Loss, Percent", stocksAggregationResult.header().joinToString(", "))
    }

    @Test
    fun itemArrays() {
        assertEquals(1, stocksAggregationResult.itemArrays().size)
    }

    @Test
    fun title() {
        assertEquals("資産運用成績", stocksAggregationResult.title())
    }

    @Test
    fun isEmpty() {
        assertFalse(stocksAggregationResult.isEmpty())
    }

    @Test
    fun columnClass() {
        assertEquals(String::class.java, stocksAggregationResult.columnClass(0))
        assertEquals(Int::class.java, stocksAggregationResult.columnClass(1))
        assertEquals(Int::class.java, stocksAggregationResult.columnClass(2))
        assertEquals(Double::class.java, stocksAggregationResult.columnClass(3))
    }
}