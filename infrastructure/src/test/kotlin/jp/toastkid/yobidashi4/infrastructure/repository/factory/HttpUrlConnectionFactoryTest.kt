package jp.toastkid.yobidashi4.infrastructure.repository.factory

import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HttpUrlConnectionFactoryTest {

    private lateinit var subject: HttpUrlConnectionFactory

    @BeforeEach
    fun setUp() {
        subject = HttpUrlConnectionFactory()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val url = mockk<URL>()
        val openConnection = URL("https://www.yahoo.co.jp").openConnection()
        every { url.openConnection() } returns openConnection

        val urlConnection = subject.invoke(url)

        verify { url.openConnection() }
        assertNotNull(urlConnection)
        (openConnection as HttpsURLConnection).disconnect()
    }

    @Test
    fun nullCase() {
        val url = mockk<URL>()
        every { url.openConnection() } returns null

        val urlConnection = subject.invoke(url)

        verify { url.openConnection() }
        assertNull(urlConnection)
    }

    @Test
    fun nullCase2() {
        val url = mockk<URL>()
        every { url.openConnection() } returns mockk<HttpURLConnection>()

        val urlConnection = subject.invoke(url)

        verify { url.openConnection() }
        assertNull(urlConnection)
    }

}