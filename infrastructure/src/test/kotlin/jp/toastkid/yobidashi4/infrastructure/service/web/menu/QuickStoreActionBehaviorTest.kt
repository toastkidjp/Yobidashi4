package jp.toastkid.yobidashi4.infrastructure.service.web.menu

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import jp.toastkid.yobidashi4.infrastructure.service.web.download.DownloadFolder
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class QuickStoreActionBehaviorTest {

    private lateinit var subject: QuickStoreActionBehavior

    @MockK
    private lateinit var url: URL

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { url.openStream() } returns "test".byteInputStream()
        every { url.file } returns "favicon.ico"

        mockkConstructor(DownloadFolder::class)
        every { anyConstructed<DownloadFolder>().makeIfNeed() } just Runs
        every { anyConstructed<DownloadFolder>().assignQuickStorePath(any()) } returns mockk()
        mockkStatic(Files::class)
        every { Files.write(any(), any<ByteArray>()) } returns mockk()

        subject = QuickStoreActionBehavior()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun obtainNullArray() {
        val inputStream = mockk<InputStream>()
        every { url.openStream() } returns inputStream
        every { inputStream.readAllBytes() } returns null

        subject.invoke(url, Dispatchers.Unconfined)

        verify(inverse = true) { anyConstructed<DownloadFolder>().makeIfNeed() }
    }

    @Test
    fun invoke() {
        subject.invoke(url, Dispatchers.Unconfined)

        verify { Files.write(any(), any<ByteArray>()) }
    }

}