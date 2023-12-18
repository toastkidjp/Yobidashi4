package jp.toastkid.yobidashi4.infrastructure.viewmodel.browser

import io.mockk.unmockkAll
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import jp.toastkid.yobidashi4.presentation.web.event.ReloadEvent
import jp.toastkid.yobidashi4.presentation.web.event.SwitchDeveloperToolEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WebTabViewModelImplementationTest {

    private lateinit var subject: WebTabViewModelImplementation

    @BeforeEach
    fun setUp() {
        subject = WebTabViewModelImplementation()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun switchDevTools() {
        val countDownLatch = CountDownLatch(1)
        CoroutineScope(Dispatchers.Unconfined).launch {
            subject.event().collect {
                assertTrue(it is SwitchDeveloperToolEvent)
                countDownLatch.countDown()
            }
        }

        subject.switchDevTools("test")

        countDownLatch.await(5, TimeUnit.SECONDS)
    }

    @Test
    fun reload() {
        val countDownLatch = CountDownLatch(1)
        CoroutineScope(Dispatchers.Unconfined).launch {
            subject.event().collect {
                assertTrue(it is ReloadEvent)
                countDownLatch.countDown()
            }
        }

        subject.reload("1")

        countDownLatch.await(5, TimeUnit.SECONDS)
    }

}