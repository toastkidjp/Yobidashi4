/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.io.file

import jp.toastkid.yobidashi4.presentation.main.content.data.FileListItemMeta
import jp.toastkid.yobidashi4.presentation.main.content.data.FileListItemMetaExtractor
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import org.koin.core.annotation.Single
import java.nio.file.Path
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Single
class FileListItemMetaExtractorImplementation(
    private val fileSystem: FileSystem
) : FileListItemMetaExtractor {

    override fun make(pathNio: Path): FileListItemMeta? {
        val path = pathNio.toOkioPath()
        if (fileSystem.exists(path).not()) {
            return null
        }

        val size = (fileSystem.metadata(path).size ?: 0L).toDouble()
        val unit = if (size > 1_048_576) "MB" else if (size > 1024) "KB" else "B"
        val displaySize = decimalFormat.format(size / (if (size > 1_048_576) 1_048_576 else if (size > 1024) 1024 else 1))
        val lastModifiedTime = fileSystem.metadata(path).lastModifiedAtMillis ?: 0L
        val sortKey = (lastModifiedTime)
        return FileListItemMeta(
            "$displaySize $unit | ${
                LocalDateTime
                    .ofInstant(Instant.ofEpochMilli(lastModifiedTime), ZoneId.systemDefault())
                    .format(dateTimeFormatter)
            }",
            sortKey
        )
    }

    private val decimalFormat = DecimalFormat("#,###.##")

    private val dateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd(E) HH:mm:ss").withLocale(Locale.ENGLISH)

}