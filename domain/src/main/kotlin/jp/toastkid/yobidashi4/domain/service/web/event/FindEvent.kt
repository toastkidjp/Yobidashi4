package jp.toastkid.yobidashi4.domain.service.web.event

data class FindEvent(
    val id: String,
    val text: String,
    val upward: Boolean = false
) : WebTabEvent {
}