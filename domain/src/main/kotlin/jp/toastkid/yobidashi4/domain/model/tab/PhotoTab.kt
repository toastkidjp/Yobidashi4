package jp.toastkid.yobidashi4.domain.model.tab

import java.nio.file.Path
import kotlin.io.path.name

class PhotoTab(
    private val path: Path
) : Tab {

    override fun title(): String {
        return path.name
    }

    fun path() = path

}