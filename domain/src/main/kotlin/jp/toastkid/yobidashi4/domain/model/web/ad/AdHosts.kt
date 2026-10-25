/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.web.ad

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.stream.Collectors

class AdHosts(private val adHosts: Set<String>) {

    fun contains(url: String?): Boolean {
        return url != null && adHosts.any(url::contains)
    }

    companion object {

        fun make(): AdHosts {
            val loadInputStream = loadInputStream() ?: return AdHosts(emptySet())

            val adHosts = BufferedReader(InputStreamReader(loadInputStream)).use { reader ->
                reader.lines().collect(Collectors.toUnmodifiableSet())
            }

            return AdHosts(adHosts)
        }

        fun loadInputStream(): InputStream? =
            AdHosts::class.java.classLoader.getResourceAsStream("web/ad_hosts.txt")

    }

}