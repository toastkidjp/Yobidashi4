package jp.toastkid.yobidashi4.domain.model.markdown

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TextBlockTest {

    private lateinit var textBlock: TextBlock

    @BeforeEach
    fun setUp() {
        textBlock = TextBlock("test",)
    }

    @Test
    fun tearDown() {
        assertEquals(7, (0..6).map { TextBlock("test", level = it).fontSize() }.size)
    }
}