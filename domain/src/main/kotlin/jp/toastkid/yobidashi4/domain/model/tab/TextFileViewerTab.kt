package jp.toastkid.yobidashi4.domain.model.tab

import java.nio.file.Path

class TextFileViewerTab(private val path: Path) : Tab {

    override fun title(): String {
        return path.fileName.toString()
    }

    override fun closeable(): Boolean {
        return true
    }

    override fun iconPath(): String {
        return "images/icon/${if (path.startsWith("temporary/logs/")) "ic_log" else "ic_text" }.xml"
    }

    fun path() = path

}