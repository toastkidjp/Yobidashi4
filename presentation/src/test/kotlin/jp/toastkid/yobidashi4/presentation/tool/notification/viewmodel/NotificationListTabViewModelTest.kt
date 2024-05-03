package jp.toastkid.yobidashi4.presentation.tool.notification.viewmodel

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.test.ExperimentalTestApi
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.invoke
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import jp.toastkid.yobidashi4.domain.repository.notification.NotificationEventRepository
import jp.toastkid.yobidashi4.domain.service.notification.ScheduledNotification
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class NotificationListTabViewModelTest {

    private lateinit var subject: NotificationListTabViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var repository: NotificationEventRepository

    @MockK
    private lateinit var notification: ScheduledNotification

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier = null) { repository } bind(NotificationEventRepository::class)
                    single(qualifier = null) { notification } bind(ScheduledNotification::class)
                }
            )
        }

        every { mainViewModel.showSnackbar(any()) } just Runs

        subject = NotificationListTabViewModel()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun focusRequester() {
        assertNotNull(subject.focusRequester())
    }

    @Test
    fun listState() {
        assertNotNull(subject.listState())
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun onKeyEvent() {
        val consumed = subject.onKeyEvent(
            CoroutineScope(Dispatchers.Unconfined),
            KeyEvent(
                java.awt.event.KeyEvent(
                    mockk(),
                    java.awt.event.KeyEvent.KEY_PRESSED,
                    1,
                    java.awt.event.KeyEvent.CTRL_DOWN_MASK,
                    java.awt.event.KeyEvent.VK_DOWN,
                    '↓'
                )
            )
        )

        assertTrue(consumed)
    }

    @Test
    fun items() {
        assertTrue(subject.items().isEmpty())
    }

    @Test
    fun add() {
        every { repository.add(any()) } just Runs

        subject.add()

        verify { repository.add(any()) }
        assertEquals(1, subject.items().size)
    }

    @Test
    fun update() {
        every { repository.update(any(), any()) } just Runs
        val slot = slot<() -> Unit>()
        every { mainViewModel.showSnackbar(any(), any(), capture(slot)) } just Runs
        coEvery { notification.start(any()) } just Runs

        subject.update(1, "title", "text", "2024-12-24 12:00:00")
        slot.invoke()

        verify { repository.update(1, any()) }
        verify { mainViewModel.showSnackbar(any(), any(), any()) }
    }

    @Test
    fun noopUpdate() {
        every { repository.update(any(), any()) } just Runs
        val slot = slot<() -> Unit>()
        every { mainViewModel.showSnackbar(any(), any(), capture(slot)) } just Runs
        coEvery { notification.start(any()) } just Runs

        subject.update(1, "title", "text", "2024-14-24 :00:00")

        assertFalse(slot.isCaptured)
        verify(inverse = true) { repository.update(1, any()) }
        verify(inverse = true) { mainViewModel.showSnackbar(any(), any(), any()) }
    }

    @Test
    fun deleteAt() {
        every { repository.add(any()) } just Runs
        every { repository.deleteAt(any()) } just Runs
        subject.add()

        subject.deleteAt(0)

        verify { repository.deleteAt(any()) }
        verify { mainViewModel.showSnackbar(any()) }
    }

    @Test
    fun start() {
        subject = spyk(subject)
        every { repository.readAll() } returns listOf(NotificationEvent.makeDefault())
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } just Runs

        subject.start(Dispatchers.Unconfined)

        verify { repository.readAll() }
        verify { focusRequester.requestFocus() }
    }
}