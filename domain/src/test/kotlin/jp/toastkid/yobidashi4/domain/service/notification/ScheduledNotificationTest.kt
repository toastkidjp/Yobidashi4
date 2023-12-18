package jp.toastkid.yobidashi4.domain.service.notification

import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ScheduledNotificationTest {

    @Test
    fun test() {
        val subject = object : ScheduledNotification {
            override fun notificationFlow(): SharedFlow<NotificationEvent> = MutableSharedFlow()

            override suspend fun start(delay: Long) = Unit
        }

        runBlocking {
            subject.start()
        }
        Assertions.assertNotNull(subject)
    }

}