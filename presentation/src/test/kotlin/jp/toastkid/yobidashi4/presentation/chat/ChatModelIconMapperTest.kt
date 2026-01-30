package jp.toastkid.yobidashi4.presentation.chat

import jp.toastkid.yobidashi4.domain.model.chat.GenerativeAiModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ChatModelIconMapperTest {

    private lateinit var subject: ChatModelIconMapper

    @BeforeEach
    fun setUp() {
        subject = ChatModelIconMapper()
    }

    @Test
    fun invoke() {
        assertEquals(
            subject.invoke(GenerativeAiModel.GEMINI_2_5_FLASH),
            subject.invoke(GenerativeAiModel.GEMINI_2_5_FLASH_LITE),
        )
    }

}