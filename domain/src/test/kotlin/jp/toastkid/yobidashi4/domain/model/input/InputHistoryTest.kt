package jp.toastkid.yobidashi4.domain.model.input

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InputHistoryTest {

    @Test
    fun toTsv() {
        val timestamp = System.currentTimeMillis()
        val tsv = InputHistory("test", timestamp).toTsv()

        assertEquals("test\t$timestamp", tsv)
    }

}