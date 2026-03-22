/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.chat

import java.net.URI

data class Source(
    val title: String,
    val url: String
) {

    private val host = extractHost()

    private fun extractHost() =
        if (url.startsWith("https://vertexaisearch.cloud.google.com"))
            title
        else
            URI(url).host

    fun host(): String = host

}