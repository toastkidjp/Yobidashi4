package jp.toastkid.yobidashi4.infrastructure.service.web.icon

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkConstructor
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.infrastructure.repository.factory.HttpUrlConnectionFactory
import okio.fakefilesystem.FakeFileSystem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.nio.file.Path

class WebIconDownloaderTest {

    private lateinit var subject: WebIconDownloader

    private lateinit var url: URL

    @MockK
    private lateinit var folder: Path

    @MockK
    private lateinit var imagePath: Path

    @MockK
    private lateinit var urlConnection: HttpURLConnection

    private lateinit var fakeFileSystem: FakeFileSystem

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        fakeFileSystem = spyk(FakeFileSystem())

        every { folder.resolve(any<String>()) } returns imagePath
        every { fakeFileSystem.exists(any()) } returns false

        url = URI("https://www.yahoo.co.jp/favicon.ico").toURL()
        every { urlConnection.responseCode } returns 200
        every { urlConnection.inputStream } returns "test".byteInputStream()

        mockkConstructor(HttpUrlConnectionFactory::class)
        every { anyConstructed<HttpUrlConnectionFactory>().invoke(any()) } returns urlConnection

        subject = WebIconDownloader(fakeFileSystem)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        subject.invoke(url, folder, "test")

        verify { fakeFileSystem.exists(any()) }
    }

    @Test
    fun urlNotIncludeExtensionCase() {
        every { folder.resolve(any<String>()) } returns imagePath
        url = URI("https://www.yahoo.co.jp/favicon").toURL()

        subject.invoke(url, folder, "test")

        verify { fakeFileSystem.exists(any()) }
        verify { folder.resolve("test.png") }
    }

    @Test
    fun urlEndDotCase() {
        every { folder.resolve(any<String>()) } returns imagePath
        url = URI("https://www.yahoo.co.jp/favicon.").toURL()

        subject.invoke(url, folder, "test")

        verify { fakeFileSystem.exists(any()) }
        verify { folder.resolve("test.png") }
    }

    @Test
    fun existsCase() {
        every { fakeFileSystem.exists(any()) } returns true

        subject.invoke(url, folder, "test")

        verify { fakeFileSystem.exists(any()) }
        verify(inverse = true) { anyConstructed<HttpUrlConnectionFactory>().invoke(any()) }
    }

    @Test
    fun connectionIsNullCase() {
        every { anyConstructed<HttpUrlConnectionFactory>().invoke(any()) } returns null

        subject.invoke(url, folder, "test")

        verify { fakeFileSystem.exists(any()) }
        verify { anyConstructed<HttpUrlConnectionFactory>().invoke(any()) }
    }
//

    @Test
    fun responseCodeIsNot200Case() {
        every { urlConnection.responseCode } returns 400

        subject.invoke(url, folder, "test")
    }

}