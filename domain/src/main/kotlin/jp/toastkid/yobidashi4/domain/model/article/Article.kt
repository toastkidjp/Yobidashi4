/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.article

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

class Article(private val file: Path) {

    private val title = file.nameWithoutExtension

    fun getTitle() = title

    fun makeFile(contentSupplier: () -> String) {
        Files.createFile(file)
        Files.write(file, contentSupplier().toByteArray(StandardCharsets.UTF_8))
    }

    fun count(): Int {
        return Files.readAllLines(file).sumOf { it.codePointCount(0, it.length) }
    }

    fun lastModified(): Long {
        return try {
            Files.getLastModifiedTime(file).toMillis()
        } catch (e: IOException) {
            0L
        }
    }

    fun path() = file

}
