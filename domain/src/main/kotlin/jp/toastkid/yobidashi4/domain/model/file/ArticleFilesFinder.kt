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
import java.nio.file.attribute.FileTime
import kotlin.streams.asSequence

class ArticleFilesFinder {

    operator fun invoke(path: Path): MutableList<Path> {
        return Files.list(path)
            .asSequence()
            .map { it to Files.getLastModifiedTime(it) }
            .sortedByDescending { it.second }
            .filter(::containsSpecificCharacters)
            .map { it.first }
            .toMutableList()
    }

    private fun containsSpecificCharacters(pair: Pair<Path, FileTime>): Boolean {
        val name = pair.first.fileName.toString()
        return name.startsWith("20") || name.startsWith("『")
    }

}
