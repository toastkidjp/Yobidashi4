package jp.toastkid.yobidashi4.infrastructure.service.web.icon

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WebIconDownloaderTest {

    private lateinit var subject: WebIconDownloader

    private lateinit var url: URL

    @MockK
    private lateinit var folder: Path

    @MockK
    private lateinit var imagePath: Path

    @MockK
    private lateinit var urlConnection: HttpURLConnection

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { folder.resolve(any<String>()) } returns imagePath
        mockkStatic(Files::class)
        every { Files.exists(any()) } returns false
        every { Files.write(any(), any<ByteArray>()) } returns mockk()

        url = URL("https://www.yahoo.co.jp/favicon.ico")
        every { urlConnection.responseCode } returns 200
        every { urlConnection.inputStream } returns "test".byteInputStream()

        subject = spyk(WebIconDownloader())
        every { subject.urlConnection(any()) } returns urlConnection
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        subject.invoke(url, folder, "test")

        verify { Files.exists(any()) }
        verify { Files.write(any(), any<ByteArray>()) }
    }

    @Test
    fun existsCase() {
        every { Files.exists(any()) } returns true

        subject.invoke(url, folder, "test")

        verify { Files.exists(any()) }
        verify(inverse = true) { subject.urlConnection(any()) }
        verify(inverse = true) { Files.write(any(), any<ByteArray>()) }
    }

}