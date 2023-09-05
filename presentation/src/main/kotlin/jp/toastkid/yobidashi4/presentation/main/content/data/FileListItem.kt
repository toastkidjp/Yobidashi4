package jp.toastkid.yobidashi4.presentation.main.content.data

import java.nio.file.Path

data class FileListItem(
    val path: Path,
    val selected: Boolean = false,
    val editable: Boolean = false
) {

    fun reverseSelection() = FileListItem(path, selected.not())

    fun unselect() = FileListItem(path, false, editable)

}