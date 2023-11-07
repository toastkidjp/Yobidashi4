package jp.toastkid.yobidashi4.infrastructure.viewmodel.browser

import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.unmockkAll
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import jp.toastkid.yobidashi4.presentation.web.event.FindEvent
import jp.toastkid.yobidashi4.presentation.web.event.ReloadEvent
import jp.toastkid.yobidashi4.presentation.web.event.SwitchDeveloperToolEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
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
    fun switchFind() {
        assertFalse(subject.openFind())

        subject.switchFind()

        assertTrue(subject.openFind())
    }

    @Test
    fun onFindInputChange() {
        val textFieldValue = TextFieldValue("test")
        val countDownLatch = CountDownLatch(1)
        CoroutineScope(Dispatchers.Unconfined).launch {
            subject.event().collect {
                assertTrue(it is FindEvent)
                assertEquals(textFieldValue.text, subject.inputValue().text)
                countDownLatch.countDown()
            }
        }

        subject.onFindInputChange("1", textFieldValue)

        countDownLatch.await(5, TimeUnit.SECONDS)
    }

    @Test
    fun findUp() {
        val textFieldValue = TextFieldValue("test")
        subject.onFindInputChange("1", textFieldValue)

        val countDownLatch = CountDownLatch(1)
        CoroutineScope(Dispatchers.Unconfined).launch {
            subject.event().collect {
                assertTrue((it as FindEvent).upward)
                countDownLatch.countDown()
            }
        }

        subject.findUp("1")

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