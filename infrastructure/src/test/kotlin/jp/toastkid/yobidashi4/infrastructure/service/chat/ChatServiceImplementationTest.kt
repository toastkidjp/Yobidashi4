package jp.toastkid.yobidashi4.infrastructure.service.chat

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.chat.Chat
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.repository.chat.ChatRepository
import jp.toastkid.yobidashi4.domain.repository.chat.dto.ChatResponseItem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class ChatServiceImplementationTest {

    private lateinit var subject: ChatServiceImplementation

    @MockK
    private lateinit var setting: Setting

    @MockK
    private lateinit var repository: ChatRepository

    @MockK
    private lateinit var callback: () -> Unit

    @MockK
    private lateinit var chat: Chat

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { setting } bind(Setting::class)
                    single(qualifier=null) { repository } bind(ChatRepository::class)
                }
            )
        }
        every { setting.chatApiKey() } returns "test-key"
        every { callback.invoke() } just Runs
        every { chat.addUserText(any()) } just Runs
        every { chat.addModelText(any()) } just Runs
        every { chat.addModelImage(any()) } just Runs

        subject = ChatServiceImplementation()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun send() {
        every { chat.makeContent(any()) } returns ""
        val capturingSlot = slot<(ChatResponseItem?) -> Unit>()
        every { repository.request(any(), capture(capturingSlot)) } just Runs
        subject.setChat(chat)

        subject.send("test", false, callback)
        capturingSlot.captured.invoke(ChatResponseItem("test"))
        capturingSlot.captured.invoke(ChatResponseItem("\"Escaped\""))
        capturingSlot.captured.invoke(ChatResponseItem("image", image = true))
        capturingSlot.captured.invoke(null)

        verify { chat.addUserText(any()) }
        verify { chat.addModelText("Escaped") }
        verify { chat.makeContent(false) }
        verify { chat.addModelImage(any()) }
        verify { repository.request(any(), any()) }
        verify { callback.invoke() }
    }

    @Test
    fun sendWhenReceiveNullResponse() {
        every { chat.makeContent(any()) } returns ""
        every { repository.request(any(), any()) } just Runs
        subject.setChat(chat)

        subject.send("test", true, callback)

        verify { chat.addUserText(any()) }
        verify(inverse = true) { chat.addModelText(any()) }
        verify { chat.makeContent(true) }
        verify { repository.request(any(), any()) }
    }

    @Test
    fun accessors() {
        assertTrue(subject.getChat().list().isEmpty())

        every { chat.list() } returns listOf(mockk(), mockk())

        subject.setChat(chat)

        val gotChat = subject.getChat()
        assertSame(chat, gotChat)
        assertEquals(2, gotChat.list().size)
        assertEquals(2, subject.messages().size)
    }

    @Test
    fun clearMessages() {
        val chat1 = subject.getChat()

        subject.clearMessages()

        assertSame(chat1, subject.getChat())
    }

}