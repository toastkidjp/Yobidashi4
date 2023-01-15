package jp.toastkid.yobidashi4.domain.model.aggregation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MovieMemoTest {

    @Test
    fun testHeader() {
        assertEquals(2, MovieMemo.header().size)
    }

    @Test
    fun testToArray() {
        assertEquals(2, MovieMemo("2021-06-27", "test").toArray().size)
    }

}