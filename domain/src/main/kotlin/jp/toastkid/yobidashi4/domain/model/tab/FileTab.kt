package jp.toastkid.yobidashi4.domain.model.tab

import java.nio.file.Path

data class FileTab(
    private val title: String,
    val items: List<Path>,
    private val closeable: Boolean = true,
    val type: Type = Type.FIND
): Tab {

    override fun title(): String = title

    override fun closeable(): Boolean = closeable

    enum class Type {
        MUSIC, FIND;
    }
}