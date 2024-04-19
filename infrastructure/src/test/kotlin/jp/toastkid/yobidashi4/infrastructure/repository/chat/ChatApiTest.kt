package jp.toastkid.yobidashi4.infrastructure.repository.chat

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import jp.toastkid.yobidashi4.infrastructure.repository.factory.HttpUrlConnectionFactory
import jp.toastkid.yobidashi4.infrastructure.service.chat.ChatStreamParser
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ChatApiTest {

    private lateinit var subject: ChatApi

    @MockK
    private lateinit var connection: HttpURLConnection

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        subject = ChatApi("test-key")

        every { connection.setRequestProperty("Content-Type", "application/json") } just Runs
        every { connection.requestMethod = any() } just Runs
        every { connection.readTimeout = any() } just Runs
        every { connection.doInput = any() } just Runs
        every { connection.doOutput = any() } just Runs
        every { connection.connect() } just Runs
        every { connection.outputStream } returns ByteArrayOutputStream()
        every { connection.inputStream } returns "test\ntest".byteInputStream()
        every { connection.errorStream } returns ByteArrayInputStream(byteArrayOf())
        every { connection.responseCode } returns 200

        mockkConstructor(ChatStreamParser::class, HttpUrlConnectionFactory::class)
        every { anyConstructed<ChatStreamParser>().invoke(any()) } returns "test"
        every { anyConstructed<HttpUrlConnectionFactory>().invoke(any()) } returns connection
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun request() {
        subject.request("{test}", {})

        verify { anyConstructed<ChatStreamParser>().invoke(any()) }
    }

    @Test
    fun requestFailureCase() {
        every { connection.responseCode } returns 500

        subject.request("{test}", {})
    }

    @Test
    fun requestConnectionNullCase() {
        every { anyConstructed<HttpUrlConnectionFactory>().invoke(any()) } returns null

        subject.request("{test}", {})
    }

    @Test
    fun requestWhenParseResultIsNullCase() {
        val consumer: (String?) -> Unit = mockk()
        every { anyConstructed<ChatStreamParser>().invoke(any()) } returns null
        every { consumer.invoke(any()) } just Runs

        subject.request("{test}", consumer)

        verify { consumer wasNot called }
    }

}