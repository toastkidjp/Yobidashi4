package jp.toastkid.yobidashi4.domain.model.aggregation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AggregationResultTest {

    @Test
    fun test() {
        val aggregationResult = object : AggregationResult {
            override fun header(): Array<Any> {
                return emptyArray()
            }

            override fun itemArrays(): Collection<Array<Any>> {
                return listOf(arrayOf(), arrayOf())
            }

            override fun title(): String {
                return "test"
            }

            override fun isEmpty(): Boolean {
                return false
            }
        }

        assertEquals(String::class.java, aggregationResult.columnClass(0))
        assertEquals(Int::class.java, aggregationResult.columnClass(1))
    }

}