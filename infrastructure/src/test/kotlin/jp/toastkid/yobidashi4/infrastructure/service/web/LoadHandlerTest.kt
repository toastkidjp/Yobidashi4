package jp.toastkid.yobidashi4.infrastructure.service.web

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.service.web.WebIconLoaderService
import org.cef.browser.CefBrowser
import org.cef.callback.CefStringVisitor
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class LoadHandlerTest {

    private lateinit var subject: LoadHandler

    @MockK
    private lateinit var webIconLoaderService: WebIconLoaderService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier = null) { webIconLoaderService } bind (WebIconLoaderService::class)
                }
            )
        }
        every { webIconLoaderService.invoke(any(), any()) } just Runs

        subject = LoadHandler()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun checkAddLoadHandler() {
        val browser = mockk<CefBrowser>()
        every { browser.url } returns "https://www.yahoo.co.jp"
        val sourceSlot = slot<CefStringVisitor>()
        every { browser.getSource(capture(sourceSlot)) } just Runs
        subject.onLoadingStateChange(browser, false, false, false)
        // For test coverage.
        subject.onLoadingStateChange(browser, true, false, false)
        every { browser.url } returns "ftp://test"
        subject.onLoadingStateChange(browser, false, false, false)
        every { browser.url } returns null
        subject.onLoadingStateChange(browser, false, false, false)
        subject.onLoadingStateChange(null, true, false, false)
        sourceSlot.captured.visit("test")

        verify { browser.getSource(any()) }
        verify { webIconLoaderService.invoke(any(), any()) }
    }
}