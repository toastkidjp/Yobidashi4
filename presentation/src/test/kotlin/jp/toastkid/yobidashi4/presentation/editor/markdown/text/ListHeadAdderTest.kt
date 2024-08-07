package jp.toastkid.yobidashi4.presentation.editor.markdown.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ListHeadAdderTest {

    private val target = """1st line
2nd line""".trimIndent()

    private val expected = """- 1st line
- 2nd line
""".trimIndent()

    private lateinit var listHeadAdder: ListHeadAdder

    @BeforeEach
    fun setUp() {
        listHeadAdder = ListHeadAdder()
    }

    @Test
    fun test() {
        assertEquals(expected, listHeadAdder.invoke(target, "-"))
    }

    @Test
    fun passNull() {
        assertNull(listHeadAdder.invoke(null, "-"))
    }

    @Test
    fun test2() {
        assertEquals(
            """- 1st line
- 2nd line
""",
            listHeadAdder.invoke(
                """1st line
2nd line
""",
                "-"
            )
        )
    }

}