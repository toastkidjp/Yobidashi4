package jp.toastkid.yobidashi4.presentation.lib.text

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KeywordHighlighterTest {

    private lateinit var subject: KeywordHighlighter

    @BeforeEach
    fun setUp() {
        subject = KeywordHighlighter()
    }

    @Test
    fun annotate() {
        val annotate = subject.invoke("It longs to ~~make~~ it.", "long")

        Assertions.assertEquals(2, annotate.spanStyles.size)
        Assertions.assertEquals("It longs to make it.", annotate.text)
    }

    @Test
    fun annotate2() {
        val annotate = subject.invoke("It **longs** to make it.", "long")

        Assertions.assertEquals(2, annotate.spanStyles.size)
        Assertions.assertEquals("It longs to make it.", annotate.text)
    }

    @Test
    fun annotate3() {
        val annotate = subject.invoke("It longs ***to*** make it.", "long")

        Assertions.assertEquals(2, annotate.spanStyles.size)
        Assertions.assertEquals("It longs to make it.", annotate.text)
    }

    @Test
    fun noopWithNull() {
        val annotate = subject.invoke("It longs to ~~make~~ it.", null)

        Assertions.assertEquals(1, annotate.spanStyles.size)
        Assertions.assertEquals("It longs to make it.", annotate.text)
    }

}