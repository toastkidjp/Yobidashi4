/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension

class IndexTargetFilter(private val indexDirectoryPath: Path) {

    private val lastIndexed = calculateLastUpdated()

    private val targetExtensions = setOf("txt", "md")

    operator fun invoke(it: Path): Boolean {
        return Files.isDirectory(it).not()
                && Files.isReadable(it)
                && targetExtensions.contains(it.extension)
                && lastModifiedMs(it) > lastIndexed
    }

    private fun calculateLastUpdated() =
        Files.list(indexDirectoryPath)
            .filter { it.nameWithoutExtension.startsWith("segments_") }
            .map(this::lastModifiedMs)
            .max(Comparator.naturalOrder())
            .orElseGet { 0L }

    private fun lastModifiedMs(paths: Path): Long = try {
        Files.getLastModifiedTime(paths).toMillis()
    } catch (e: IOException) {
        0L
    }

}