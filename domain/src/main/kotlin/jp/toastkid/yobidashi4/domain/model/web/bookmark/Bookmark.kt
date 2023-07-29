package jp.toastkid.yobidashi4.domain.model.web.bookmark

data class Bookmark(
    val title: String = "",
    val url: String = "",
    val favicon: String = "",
    val parent: String = "root",
    val folder: Boolean = false
) {
}