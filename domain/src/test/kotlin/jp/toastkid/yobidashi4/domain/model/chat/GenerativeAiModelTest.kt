package jp.toastkid.yobidashi4.domain.model.chat

import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GenerativeAiModelTest {

    @Test
    fun image() {
        assertTrue(GenerativeAiModel.GEMINI_2_0_FLASH_IMAGE.image())

        assertTrue(
            GenerativeAiModel.entries
                .filter { it != GenerativeAiModel.GEMINI_2_0_FLASH_IMAGE }
                .none(GenerativeAiModel::image)
        )
    }

    @Test
    fun default() {
        assertSame(GenerativeAiModel.GEMINI_2_5_FLASH_LITE, GenerativeAiModel.default())
    }

}