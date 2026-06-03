/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.file

import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.asSequence

class ArticleFilesFinder {

    operator fun invoke(path: Path): MutableList<Path> {
        return Files.list(path)
            .asSequence()
            .map { it to Files.getLastModifiedTime(it) }
            .sortedByDescending { it.second }
            .filter {
                val name = it.first.fileName.toString()
                name.startsWith("20") || name.startsWith("『")
            }
            .map { it.first }
            .toMutableList()
    }

    private fun compareByLastModified(p1: Path, p2: Path): Int =
        Files.getLastModifiedTime(p1).compareTo(Files.getLastModifiedTime(p2)) * -1

}