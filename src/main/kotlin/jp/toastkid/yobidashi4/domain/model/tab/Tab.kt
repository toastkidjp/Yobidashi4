package jp.toastkid.yobidashi4.domain.model.tab

interface Tab {

    fun title(): String

    fun closeable(): Boolean = true

    fun iconPath(): String? = null

}