package jp.toastkid.yobidashi4.domain.model.tab

import java.nio.file.Path

class TextFileViewerTab(private val path: Path) : Tab {

    override fun title(): String {
        return path.fileName.toString()
    }

    fun path() = path

}