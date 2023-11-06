package jp.toastkid.yobidashi4.presentation.editor.finder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FindOrderTest {

    @Test
    fun test() {
        val findOrder = FindOrder("test", "replacement")

        assertEquals("test",  findOrder.target)
        assertEquals("replacement",  findOrder.replace)
        assertTrue(findOrder.caseSensitive)
        assertFalse(findOrder.invokeReplace)
        assertFalse(findOrder.upper)
        assertNotEquals(findOrder, FindOrder.EMPTY)
    }

}