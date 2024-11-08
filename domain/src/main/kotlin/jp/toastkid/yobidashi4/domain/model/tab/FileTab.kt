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

    enum class Type(private val iconPath: String) {
        MUSIC("images/icon/ic_music.xml"), FIND("images/icon/ic_search.xml");

        fun iconPath() = iconPath
    }
}