package jp.toastkid.yobidashi4.presentation.chat

import jp.toastkid.yobidashi4.domain.model.chat.GenerativeAiModel
import org.junit.jupiter.api.Assertions.assertNotEquals
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
        assertNotEquals(
            subject.invoke(GenerativeAiModel.GEMINI_2_0_FLASH),
            subject.invoke(GenerativeAiModel.GEMINI_2_0_FLASH_IMAGE)
        )
    }

}