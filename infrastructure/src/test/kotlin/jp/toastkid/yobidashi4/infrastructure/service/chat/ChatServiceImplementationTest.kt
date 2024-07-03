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

        subject = ChatServiceImplementation()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun send() {
        val chat = mockk<Chat>()
        every { chat.addUserText(any()) } just Runs
        every { chat.addModelText(any()) } just Runs
        every { chat.makeContent() } returns ""
        val capturingSlot = slot<(String?) -> Unit>()
        every { repository.request(any(), capture(capturingSlot)) } just Runs
        subject.setChat(chat)

        subject.send("test")
        capturingSlot.captured.invoke("test")
        capturingSlot.captured.invoke(null)

        verify { chat.addUserText(any()) }
        verify { chat.makeContent() }
        verify { repository.request(any(), any()) }
    }

    @Test
    fun sendWhenReceiveNullResponse() {
        val chat = mockk<Chat>()
        every { chat.addUserText(any()) } just Runs
        every { chat.addModelText(any()) } just Runs
        every { chat.makeContent() } returns ""
        every { repository.request(any(), any()) } just Runs
        subject.setChat(chat)

        subject.send("test")

        verify { chat.addUserText(any()) }
        verify(inverse = true) { chat.addModelText(any()) }
        verify { chat.makeContent() }
        verify { repository.request(any(), any()) }
    }

    @Test
    fun accessors() {
        assertTrue(subject.getChat().list().isEmpty())

        val chat = mockk<Chat>()
        every { chat.list() } returns listOf(mockk(), mockk())

        subject.setChat(chat)

        val gotChat = subject.getChat()
        assertSame(chat, gotChat)
        assertEquals(2, gotChat.list().size)
        assertEquals(2, subject.messages().size)
    }

}