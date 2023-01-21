package jp.toastkid.yobidashi4.domain.model.tab

import java.nio.file.Path

data class FileTab(
    private val title: String,
    val items: List<Path>,
    private val closeable: Boolean = true,
    private val type: Type = Type.ARTICLE
): Tab {

    override fun title(): String = title

    override fun closeable(): Boolean = closeable

    override fun iconPath(): String? {
        return type.iconPath()
    }

    enum class Type(private val iconPath: String) {
        ARTICLE("images/icon/ic_edit.xml"), MUSIC("images/icon/ic_music.xml"), FIND("images/icon/ic_search.xml");

        fun iconPath() = iconPath
    }
}