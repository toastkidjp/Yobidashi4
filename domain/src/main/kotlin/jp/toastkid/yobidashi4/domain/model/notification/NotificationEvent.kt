package jp.toastkid.yobidashi4.domain.model.notification

import java.time.LocalDateTime

data class NotificationEvent(
    val title: String,
    val text: String,
    val date: LocalDateTime
)