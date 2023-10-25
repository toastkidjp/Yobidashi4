package jp.toastkid.yobidashi4.presentation.editor.markdown.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class NumberedListHeadAdderTest {

    private val target = """1st line
2nd line""".trimIndent()

    private val expected = """1. 1st line
2. 2nd line
""".trimIndent()

    @Test
    fun test() {
        assertEquals(expected, NumberedListHeadAdder().invoke(target))
    }

    @Test
    fun testNullCase() {
        assertNull(NumberedListHeadAdder().invoke(null))
    }

    @Test
    fun test2() {
        assertEquals(
            """1. 1st line
2. 2nd line
""",
            NumberedListHeadAdder().invoke(
                """1st line
2nd line
"""
            )
        )
    }
}