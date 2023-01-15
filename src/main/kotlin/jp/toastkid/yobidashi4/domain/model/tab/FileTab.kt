package jp.toastkid.yobidashi4.domain.model.tab

import java.nio.file.Path

data class FileTab(
    private val title: String,
    val items: List<Path>,
    private val closeable: Boolean = true
): Tab {

    override fun title(): String = title

    override fun closeable(): Boolean = closeable

}