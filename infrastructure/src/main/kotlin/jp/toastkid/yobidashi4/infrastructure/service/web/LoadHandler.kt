package jp.toastkid.yobidashi4.infrastructure.service.web

import jp.toastkid.yobidashi4.domain.service.web.WebIconLoaderService
import org.cef.browser.CefBrowser
import org.cef.handler.CefLoadHandlerAdapter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoadHandler : CefLoadHandlerAdapter(), KoinComponent {

    private val webIconLoaderService: WebIconLoaderService by inject()

    override fun onLoadingStateChange(
        browser: CefBrowser?,
        isLoading: Boolean,
        canGoBack: Boolean,
        canGoForward: Boolean
    ) {
        super.onLoadingStateChange(browser, isLoading, canGoBack, canGoForward)
        if (browser == null) {
            return
        }

        if (isLoading.not() && browser.url?.startsWith("http") == true) {
            browser.getSource {
                webIconLoaderService.invoke(it, browser.url)
            }
        }
    }

}