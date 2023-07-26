package jp.toastkid.yobidashi4.infrastructure.model.web

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ContextMenuTest {

    @Test
    fun id() {
        val values = ContextMenu.values()

        assertEquals(values.size, values.map { it.id }.toSet().size)
    }

}