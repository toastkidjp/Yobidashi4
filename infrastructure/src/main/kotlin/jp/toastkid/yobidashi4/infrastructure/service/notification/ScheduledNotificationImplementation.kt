package jp.toastkid.yobidashi4.infrastructure.service.notification

import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean
import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import jp.toastkid.yobidashi4.domain.repository.notification.NotificationEventRepository
import jp.toastkid.yobidashi4.domain.service.notification.ScheduledNotification
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class ScheduledNotificationImplementation : ScheduledNotification, KoinComponent {

    private val _notificationFlow = MutableSharedFlow<NotificationEvent>()

    private val repository: NotificationEventRepository by inject()

    private val running = AtomicBoolean(false)

    private val notificationEvents = mutableSetOf<NotificationEvent>()

    override fun notificationFlow() = _notificationFlow.asSharedFlow()

    override suspend fun start(delay: Long) {
        notificationEvents.addAll(repository.readAll())
        if (running.get()) {
            return
        }

        running.set(true)
        while (notificationEvents.isNotEmpty()) {
            val iterator = notificationEvents.iterator()
            for (event in iterator) {
                if (LocalDateTime.now().isAfter(event.date).not()) {
                    continue
                }
                _notificationFlow.emit(event)
                iterator.remove()
            }
            delay(delay)
        }
        running.set(false)
    }

}