package jp.toastkid.yobidashi4.infrastructure.service.web

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
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

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mockkConstructor(WebIconDownloader::class, IconUrlFinder::class, WebIcon::class)
        every { anyConstructed<WebIconDownloader>().invoke(any(), any(), any()) } just Runs
        every { anyConstructed<IconUrlFinder>().invoke(any()) } returns listOf("https://www.yahoo.co.jp/icon.svg")
        every { anyConstructed<WebIcon>().makeFolderIfNeed() } just Runs

        subject = WebIconLoaderServiceImplementation()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        subject.invoke("", "https://www.yahoo.co.jp")

        verify { anyConstructed<WebIconDownloader>().invoke(any(), any(), any()) }
    }
}