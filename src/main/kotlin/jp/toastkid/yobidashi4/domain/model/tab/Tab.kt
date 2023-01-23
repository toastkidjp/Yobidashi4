package jp.toastkid.yobidashi4.domain.model.tab

interface Tab {

    fun title(): String

    fun closeable(): Boolean

    fun iconPath(): String? = null

}