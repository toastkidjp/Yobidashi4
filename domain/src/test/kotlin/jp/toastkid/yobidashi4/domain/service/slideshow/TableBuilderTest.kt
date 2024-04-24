package jp.toastkid.yobidashi4.domain.service.slideshow

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TableBuilderTest {

    @Test
    fun elseCase() {
        assertTrue(TableBuilder().build().header.isEmpty())
    }

    @Test
    fun active() {
        val tableBuilder = TableBuilder()

        assertFalse(tableBuilder.active())

        tableBuilder.setActive()
        assertTrue(tableBuilder.active())

        tableBuilder.setInactive()
        assertFalse(tableBuilder.active())
    }

}