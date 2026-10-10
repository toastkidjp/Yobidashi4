/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.web

import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.domain.service.web.WebIconLoaderService
import jp.toastkid.yobidashi4.infrastructure.service.web.icon.IconUrlFinder
import jp.toastkid.yobidashi4.infrastructure.service.web.icon.WebIconDownloader
import okio.FileSystem
import org.koin.core.annotation.Single
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URL

@Single
class WebIconLoaderServiceImplementation(
    private val fileSystem: FileSystem,
    private val webIconDownloader: WebIconDownloader = WebIconDownloader(fileSystem),
    private val webIcon: WebIcon = WebIcon(),
    private val iconUrlFinder: IconUrlFinder = IconUrlFinder()
) : WebIconLoaderService {

    override operator fun invoke(htmlSource: String, browserUrl: String?) {
        val iconUrls = iconUrlFinder.invoke(htmlSource).toMutableList()

        webIcon.makeFolderIfNeed()

        val targetUrl = extractTargetUri(browserUrl)
        val baseUrl = if (targetUrl == null) "" else "${targetUrl.protocol}://${targetUrl.host}"

        if (iconUrls.isEmpty() && baseUrl.isNotBlank()) {
            iconUrls.add("$baseUrl/favicon.ico")
        }

        if (iconUrls.size > 1) {
            iconUrls.removeIf { it.endsWith(".ico") }
        }

        val targetHost = targetUrl?.host
        iconUrls.mapNotNull {
            toUrl(
                if (it.startsWith("/") && baseUrl.isNotBlank()) {
                    "$baseUrl$it"
                } else {
                    it
                }
            )
        }
            .forEach { webIconDownloader(it, webIcon.faviconFolder(), targetHost) }
    }

    private fun toUrl(it: String): URL? {
        val uri = URI(it)
        if (uri.isAbsolute.not()) {
            LoggerFactory.getLogger(javaClass).warn("Not absolute url: $it")
            return null
        }
        return uri.toURL()
    }

    private fun extractTargetUri(uriString: String?): URL? {
        if (uriString.isNullOrBlank()) {
            return null
        }

        return toUrl(uriString)
    }

}