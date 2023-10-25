package jp.toastkid.yobidashi4.presentation.editor.markdown.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ListHeadAdderTest {

    private val target = """1st line
2nd line""".trimIndent()

    private val expected = """- 1st line
- 2nd line
""".trimIndent()

    @Test
    fun test() {
        assertEquals(expected, ListHeadAdder().invoke(target, "-"))
    }

    @Test
    fun test2() {
        assertEquals(
            """- 1st line
- 2nd line
""",
            ListHeadAdder().invoke(
                """1st line
2nd line
""",
                "-"
            )
        )
    }

}