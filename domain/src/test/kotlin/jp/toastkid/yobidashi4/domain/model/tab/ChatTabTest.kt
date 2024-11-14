package jp.toastkid.yobidashi4.domain.model.tab

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.chat.Chat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ChatTabTest {

    private lateinit var subject: ChatTab

    @MockK
    private lateinit var chat: Chat

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        subject = ChatTab(chat)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun title() {
        assertTrue(subject.title().isNotEmpty())
    }

    @Test
    fun iconPath() {
        assertNull(subject.iconPath())
    }

    @Test
    fun chat() {
        assertSame(chat, subject.chat())
    }

}