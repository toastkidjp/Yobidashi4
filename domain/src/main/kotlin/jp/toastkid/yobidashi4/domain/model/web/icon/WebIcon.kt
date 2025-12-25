/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.web.icon

import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.io.path.pathString

class WebIcon {

    private val faviconFolder = Path.of("temporary/web/icon")

    fun makeFolderIfNeed() {
        if (Files.exists(faviconFolder).not()) {
            Files.createDirectories(faviconFolder)
        }
    }

    fun faviconFolder(): Path = faviconFolder

    fun find(url: String): Path? {
        if (!url.startsWith("http")) {
            return null
        }

        val host = URI(url).host ?: return null
        val uri = host.trim()

        return Files.list(faviconFolder).collect(Collectors.toList()).firstOrNull {
            val startsWith = it.fileName.pathString.startsWith(uri)
            startsWith
        }
    }

    fun readAll(): List<Path> =
        Files.list(faviconFolder).collect(Collectors.toList())

}