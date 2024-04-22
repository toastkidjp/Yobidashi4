package jp.toastkid.yobidashi4.infrastructure.repository.chat

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.chat.Chat
import jp.toastkid.yobidashi4.domain.repository.chat.ChatExporter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ChatExporterImplementationTest {

    private lateinit var subject: ChatExporter

    @MockK
    private lateinit var chat: Chat

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Files::class)
        every { Files.exists(any()) } returns false
        every { Files.createDirectories(any()) } returns mockk()
        every { Files.write(any(), any<ByteArray>()) } returns mockk()

        every { chat.makeContent() } returns "test"

        subject = ChatExporterImplementation()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        subject.invoke(chat)

        verify { Files.exists(any()) }
        verify { Files.createDirectories(any()) }
        verify { Files.write(any(), any<ByteArray>()) }
    }

    @Test
    fun withoutCreateDirectories() {
        every { Files.exists(any()) } returns true

        subject.invoke(chat)

        verify { Files.exists(any()) }
        verify(inverse = true) { Files.createDirectories(any()) }
        verify { Files.write(any(), any<ByteArray>()) }
    }

}