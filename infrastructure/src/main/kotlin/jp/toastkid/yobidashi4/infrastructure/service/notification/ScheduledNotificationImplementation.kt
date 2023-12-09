package jp.toastkid.yobidashi4.infrastructure.service.notification

import java.time.LocalDateTime
import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import jp.toastkid.yobidashi4.domain.service.notification.NotificationEventReader
import jp.toastkid.yobidashi4.domain.service.notification.ScheduledNotification
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent

@Single
class ScheduledNotificationImplementation : ScheduledNotification, KoinComponent {

    private val _notificationFlow = MutableSharedFlow<NotificationEvent>()

    private val notificationEventReader = NotificationEventReader()

    private var running = false

    private val notificationEvents = mutableSetOf<NotificationEvent>()

    override fun notificationFlow() = _notificationFlow.asSharedFlow()

    override suspend fun start() {
        notificationEvents.addAll(notificationEventReader.invoke())
        if (running) {
            return
        }

        running = true
        while (notificationEvents.isNotEmpty()) {
            val iterator = notificationEvents.iterator()
            for (event in iterator) {
                if (LocalDateTime.now().isAfter(event.date).not()) {
                    continue
                }
                _notificationFlow.emit(event)
                iterator.remove()
            }
            delay(10_000)
        }
        running = false
    }

}