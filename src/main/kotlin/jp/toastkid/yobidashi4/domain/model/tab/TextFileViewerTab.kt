package jp.toastkid.yobidashi4.domain.model.tab

import java.nio.file.Path

class TextFileViewerTab(private val path: Path) : Tab {

    override fun title(): String {
        return path.fileName.toString()
    }

    override fun closeable(): Boolean {
        return true
    }

    override fun iconPath(): String? {
        return "images/icon/ic_log.xml"
    }

    fun path() = path

}