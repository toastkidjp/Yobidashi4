package jp.toastkid.yobidashi4.infrastructure.repository.chat

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.spyk
import io.mockk.unmockkAll
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ChatApiTest {

    private lateinit var subject: ChatApi

    @MockK
    private lateinit var connection: HttpURLConnection

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        subject = spyk(ChatApi("test-key"))

        every { subject.openConnection() } returns connection
        every { connection.setRequestProperty("Content-Type", "application/json") } just Runs
        every { connection.requestMethod = any() } just Runs
        every { connection.doInput = any() } just Runs
        every { connection.doOutput = any() } just Runs
        every { connection.connect() } just Runs
        every { connection.outputStream } returns ByteArrayOutputStream()
        every { connection.inputStream } returns ByteArrayInputStream(byteArrayOf())
        every { connection.errorStream } returns ByteArrayInputStream(byteArrayOf())
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

    @Test
    fun requestFailureCase() {
        every { connection.responseCode } returns 500

        val response = subject.request("{test}")

        assertNull(response)
    }

    @Test
    fun requestConnectionNullCase() {
        every { subject.openConnection() } returns null

        val response = subject.request("{test}")

        assertNull(response)
    }

}