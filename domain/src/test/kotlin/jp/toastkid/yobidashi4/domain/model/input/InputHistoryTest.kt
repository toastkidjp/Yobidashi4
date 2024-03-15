package jp.toastkid.yobidashi4.domain.model.input

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class InputHistoryTest {

    @Test
    fun toTsv() {
        val timestamp = System.currentTimeMillis()
        val tsv = InputHistory("test", timestamp).toTsv()

        assertEquals("test\t$timestamp", tsv)
    }

    @Test
    fun from() {
        val timestamp = System.currentTimeMillis()
        val text = "test\t$timestamp"

        val inputHistory = InputHistory.from(text) ?: fail("This case must return non-null value.")

        assertEquals("test", inputHistory.word)
        assertEquals(timestamp, inputHistory.timestamp)
    }

    @Test
    fun fromReturnsNullCase() {
        assertNull(InputHistory.from(null))
    }

    @Test
    fun withWord() {
        val inputHistory = InputHistory.withWord("test")

        assertEquals("test", inputHistory.word)
    }

}