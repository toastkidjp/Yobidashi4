/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.content.data

import java.nio.file.Path
import kotlin.io.path.extension

class FileListItemFactory(
    private val metaDataExtractor: FileListItemMetaExtractor
) {

    operator fun invoke(
        path: Path,
        selected: Boolean = false
    ): FileListItem {
        val listItemMeta = metaDataExtractor.make(path)
        return FileListItem(path, selected, editableExtensions.contains(path.extension), listItemMeta?.subText, listItemMeta?.lastModified ?: 0L)
    }

}

private val editableExtensions = setOf("md", "txt")
