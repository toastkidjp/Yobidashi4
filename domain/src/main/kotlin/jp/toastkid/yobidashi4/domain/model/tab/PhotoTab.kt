package jp.toastkid.yobidashi4.domain.model.tab

import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.name

class PhotoTab(
    private val path: Path
) : Tab {

    override fun title(): String {
        return path.name
    }

    override fun iconPath() = path.absolutePathString()

    fun path() = path

}