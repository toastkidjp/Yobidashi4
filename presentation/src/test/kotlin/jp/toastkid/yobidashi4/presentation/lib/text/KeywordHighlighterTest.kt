package jp.toastkid.yobidashi4.presentation.lib.text

import androidx.compose.ui.text.style.TextDecoration.Companion.Underline
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

        assertEquals(2, annotate.spanStyles.size)
        assertEquals("It longs to make it.", annotate.text)
    }

    @Test
    fun boldingCase() {
        val annotate = subject.invoke("It **longs** to make it.", "long")

        assertEquals(2, annotate.spanStyles.size)
        assertEquals("It longs to make it.", annotate.text)
    }

    @Test
    fun italicCase() {
        val annotate = subject.invoke("It longs ***to*** make it.", "long")

        assertEquals(2, annotate.spanStyles.size)
        assertEquals("It longs to make it.", annotate.text)
    }

    @Test
    fun noopWithNull() {
        val annotate = subject.invoke("It longs to ~~make~~ it.", null)

        assertEquals(1, annotate.spanStyles.size)
        assertEquals("It longs to make it.", annotate.text)
    }

    @Test
    fun link() {
        val annotate = subject.invoke("It [longs](https://www.yahoo.co.jp) to make it.", null)

        assertEquals(1, annotate.spanStyles.size)
        assertTrue(annotate.spanStyles.any { it.item.textDecoration == Underline })
        assertEquals("It longs to make it.", annotate.text)
    }

    @Test
    fun linkAndTextCase() {
        val annotate = subject.invoke(
            "- [Easter Egg in APK Files: What Is Frosting]" +
                    "(https://bi-zone.medium.com/easter-egg-in-apk-files-what-is-frosting-f356aa9f4d1)" +
                    "……いわゆる雪エフェクトの実装方法についてだった",
            "雪"
        )
        assertEquals(2, annotate.spanStyles.size)
        assertTrue(annotate.spanStyles.any { it.item.textDecoration == Underline })
        assertEquals(
            "- Easter Egg in APK Files: What Is Frosting……いわゆる雪エフェクトの実装方法についてだった",
            annotate.text
        )
    }

}