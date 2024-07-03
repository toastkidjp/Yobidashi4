package jp.toastkid.yobidashi4.domain.model.aggregation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StepsAggregationResultTest {

    private lateinit var subject: StepsAggregationResult

    @BeforeEach
    fun setUp() {
        subject = StepsAggregationResult()
    }

    @Test
    fun properties() {
        assertTrue(subject.title().isNotBlank())
        assertTrue(subject.isEmpty())
    }

    @Test
    fun columnClass() {
        assertEquals(String::class.java, subject.columnClass(0))
        assertEquals(Int::class.java, subject.columnClass(1))
        assertEquals(Int::class.java, subject.columnClass(2))
    }

}