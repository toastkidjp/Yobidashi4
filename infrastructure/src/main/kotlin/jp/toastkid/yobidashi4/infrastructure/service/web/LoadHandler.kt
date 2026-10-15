/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.web

import jp.toastkid.yobidashi4.domain.service.web.WebIconLoaderService
import org.cef.browser.CefBrowser
import org.cef.handler.CefLoadHandlerAdapter
import org.koin.core.component.KoinComponent

class LoadHandler(
    private val webIconLoaderService: WebIconLoaderService
) : CefLoadHandlerAdapter(), KoinComponent {

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