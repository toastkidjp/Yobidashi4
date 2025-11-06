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
import java.util.stream.Collectors

class ArticleFilesFinder {

    operator fun invoke(path: Path): MutableList<Path> {
        return Files.list(path)
            .sorted { p1, p2 -> Files.getLastModifiedTime(p1).compareTo(Files.getLastModifiedTime(p2)) * -1 }
            .filter {
                val name = it.fileName.toString()
                name.startsWith("20") || name.startsWith("『")
            }
            .collect(Collectors.toList())
    }

}