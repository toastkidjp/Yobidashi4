package jp.toastkid.yobidashi4.infrastructure.service.notification

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import jp.toastkid.yobidashi4.domain.repository.notification.NotificationEventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class ScheduledNotificationImplementationTest {

    private lateinit var subject: ScheduledNotificationImplementation

    @MockK
    private lateinit var repository: NotificationEventRepository

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single(qualifier = null) { repository } bind(NotificationEventRepository::class)
                }
            )
        }

        MockKAnnotations.init(this)

        subject = ScheduledNotificationImplementation()

        val today = LocalDateTime.now()
        every { repository.readAll() } returns listOf(
            NotificationEvent("test", "test", today.minusMinutes(3)),
            NotificationEvent("fail", "fail", today.plusYears(10))
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun start() {
        val countDownLatch = CountDownLatch(1)
        val job = CoroutineScope(Dispatchers.Unconfined).launch {
            subject.notificationFlow().collect {
                assertEquals("test", it.title)
                assertEquals("test", it.text)
                countDownLatch.countDown()
            }
        }

        val sendJob = CoroutineScope(Dispatchers.Unconfined).launch {
            subject.start(1000)
            every { repository.readAll() } returns emptyList()
            subject.start(1)
        }

        countDownLatch.await(2, TimeUnit.SECONDS)
        job.cancel()
        sendJob.cancel()
    }

}