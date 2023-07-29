package jp.toastkid.yobidashi4.domain.model.list

import java.nio.file.Path

data class FileListItem(
    val path: Path,
    val selected: Boolean = false
) {

    fun reverseSelection() = FileListItem(path, selected.not())

    fun unselect() = FileListItem(path, false)

}