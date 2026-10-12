/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.viewmodel.main

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import org.slf4j.LoggerFactory

class DroppedPathsFlatMapper(
    private val fileSystem: FileSystem
) {

    operator fun invoke(p: java.nio.file.Path): List<Path> {
        val path = p.toOkioPath()
        if (fileSystem.metadata(path).isRegularFile) {
            return listOf(path)
        }

        if (fileSystem.metadata(path).isDirectory) {
            return try {
                fileSystem.listRecursively(path)
                    .filter { fileSystem.metadata(it).isRegularFile }
                    .toList()
            } catch (e: Exception) {
                LoggerFactory.getLogger(javaClass).warn("I/O error", e)
                return emptyList()
            }
        }

        return emptyList<okio.Path>()
    }

}
