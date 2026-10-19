package jp.toastkid.yobidashi4.domain.model.calendar.holiday

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LabelTest {

    @Test
    fun dayOfMonth() {
        assertEquals(1, Holiday("test", 1, 1).dayOfMonth())
    }

}