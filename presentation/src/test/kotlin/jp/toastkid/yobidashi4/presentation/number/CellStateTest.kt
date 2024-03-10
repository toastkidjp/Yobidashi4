package jp.toastkid.yobidashi4.presentation.number

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CellStateTest {

    @Test
    fun text() {
        assertEquals("_", CellState(-1).text())
        assertEquals("1", CellState(1).text())
    }

}