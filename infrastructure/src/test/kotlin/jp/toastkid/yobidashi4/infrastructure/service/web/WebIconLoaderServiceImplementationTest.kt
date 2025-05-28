package jp.toastkid.yobidashi4.infrastructure.service.web

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.infrastructure.service.web.icon.IconUrlFinder
import jp.toastkid.yobidashi4.infrastructure.service.web.icon.WebIconDownloader
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WebIconLoaderServiceImplementationTest {

    private lateinit var subject: WebIconLoaderServiceImplementation

    @MockK
    private lateinit var webIconDownloader: WebIconDownloader

    @MockK
    private lateinit var webIcon: WebIcon

    @MockK
    private lateinit var iconUrlFinder: IconUrlFinder

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { webIconDownloader.invoke(any(), any(), any()) } just Runs
        every { webIcon.makeFolderIfNeed() } just Runs
        every { webIcon.faviconFolder() } returns mockk()
        every { iconUrlFinder.invoke(any()) } returns listOf("https://www.yahoo.co.jp/icon.svg")

        subject = WebIconLoaderServiceImplementation(webIconDownloader, webIcon, iconUrlFinder)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        subject.invoke("", "https://www.yahoo.co.jp")

        verify { webIconDownloader.invoke(any(), any(), any()) }
    }

    @Test
    fun notAbsoluteInputCase() {
        every { iconUrlFinder.invoke(any()) } returns listOf("icon.svg")

        subject.invoke("", "/test")

        verify(inverse = true) { webIconDownloader.invoke(any(), any(), any()) }
    }

    @Test
    fun iconUrlsIsEmptyCase() {
        every { iconUrlFinder.invoke(any()) } returns emptyList()

        subject.invoke("", "https://www.yahoo.co.jp")

        verify { webIconDownloader.invoke(any(), any(), any()) }
    }

    @Test
    fun pluralIconUrlCase() {
        every { iconUrlFinder.invoke(any()) } returns listOf("https://www.yahoo.co.jp/icon.svg", "https://www.yahoo.co.jp/icon.ico", "/test.png")

        subject.invoke("", "https://www.yahoo.co.jp")

        verify { webIconDownloader.invoke(any(), any(), any()) }
    }

}