/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.model.file

import jp.toastkid.yobidashi4.domain.model.file.LatestFileFinder
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import org.koin.core.annotation.Single
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.OffsetDateTime

@Single
class LatestFileFinderImplementation(
    private val fileSystem: FileSystem
) : LatestFileFinder {

    override operator fun invoke(path: Path, latest: LocalDateTime): MutableList<Path> {
        val toEpochMilli =  latest.toInstant(OffsetDateTime.now().offset).toEpochMilli()
        return fileSystem
            .list(path.toOkioPath())
            .asSequence()
            .map { it to (fileSystem.metadata(it).lastModifiedAtMillis ?: -1L) }
            .sortedByDescending { it.second }
            .filter { it.second > toEpochMilli }
            .map { it.first.toNioPath() }
            .toMutableList()
    }

}