package jp.toastkid.yobidashi4.infrastructure.service.clustering

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BigramGeneratorTest {

    private lateinit var subject: BigramGenerator

    @BeforeEach
    fun setUp() {
        subject = BigramGenerator()
    }

    @Test
    fun filterOut() {
        val bigrams = subject.invoke(
            "id1" to "室温 室温 室温 室温" + // ストップワード
                    "ab ab ab ab" +    // 正規表現(漢字/ひらがな/カタカナ2文字)に不適合
                    "ああああ"          // 漢字を1文字も含まない (isKanjiCharacter)
        )

        assertTrue(bigrams.isEmpty())
    }

    @Test
    fun invoke() {
        val bigrams = subject.invoke(
            "id1" to "漢字漢字漢字漢字"
        )

        assertEquals(7, bigrams.size)
    }

    @Test
    fun edgeCases() {
        val bigrams = subject.invoke(
            "id1" to "\u4DFF\u9FFF\u4E00\uA000"
        )

        assertEquals(1, bigrams.size)
    }

}