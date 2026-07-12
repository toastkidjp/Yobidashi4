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
import kotlin.io.path.nameWithoutExtension

@Immutable
data class FileListItem(
    val path: Path,
    val selected: Boolean = false,
    val editable: Boolean = false,
    private val subText: String? = null,
    private val sortKey: Long = -1L
) {

    fun reverseSelection() = copy(selected = selected.not())

    fun unselect() = copy(selected = false)

    fun name() = path.nameWithoutExtension

    fun subText(): String? = subText

    fun sortKey(): Long = sortKey

    fun keep() = !subText.isNullOrBlank()

}
