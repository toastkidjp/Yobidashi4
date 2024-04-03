package jp.toastkid.yobidashi4.infrastructure.repository.chat

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ChatApiTest {

    private lateinit var subject: ChatApi

    @BeforeEach
    fun setUp() {
        subject = spyk(ChatApi("test-key"))

        val connection = mockk<HttpURLConnection>()
        every { subject.openConnection() } returns connection
        every { connection.setRequestProperty("Content-Type", "application/json") } just Runs
        every { connection.requestMethod = any() } just Runs
        every { connection.doInput = any() } just Runs
        every { connection.doOutput = any() } just Runs
        every { connection.connect() } just Runs
        every { connection.outputStream } returns ByteArrayOutputStream()
        every { connection.inputStream } returns ByteArrayInputStream(byteArrayOf())
        every { connection.responseCode } returns 200
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun request() {
        subject.request("{test}")
    }
}