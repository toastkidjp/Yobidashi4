package jp.toastkid.yobidashi4.domain.service.notification

import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import kotlinx.coroutines.flow.SharedFlow

interface ScheduledNotification {

    fun notificationFlow(): SharedFlow<NotificationEvent>

    suspend fun start()

}