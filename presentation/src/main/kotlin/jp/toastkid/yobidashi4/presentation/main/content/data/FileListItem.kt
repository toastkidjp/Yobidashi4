/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.content.data

import androidx.compose.runtime.Immutable
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import kotlin.io.path.nameWithoutExtension

@Immutable
data class FileListItem(
    val path: Path,
    val selected: Boolean = false,
    val editable: Boolean = false,
) {

    private val subText = AtomicReference("")

    private val sortKey = AtomicLong(-1L)

    fun setMeta(meta: FileListItemMeta) {
        subText.set(meta.subText)
        sortKey.set(meta.lastModified)
    }

    fun reverseSelection() = FileListItem(path, selected.not(), editable)

    fun unselect() = FileListItem(path, false, editable)

    fun name() = path.nameWithoutExtension

    fun subText(): String? = subText.get()

    fun sortKey(): Long = sortKey.get()

    fun keep() = !subText.get().isNullOrBlank()

}
