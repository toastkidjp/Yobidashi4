package jp.toastkid.yobidashi4.presentation.main

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import jp.toastkid.yobidashi4.domain.service.notification.ScheduledNotification
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class MainApplicationViewModelTest {

    @InjectMockKs
    private lateinit var viewModel: MainApplicationViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var notification: ScheduledNotification

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { mainViewModel.windowVisible() } returns true
        coEvery { notification.start() } just Runs
        every { mainViewModel.sendNotification(any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun windowVisible() {
        assertTrue(viewModel.windowVisible())
    }

    @Test
    fun startNotification() {
        runTest {
            viewModel.startNotification()

            coVerify { notification.start() }
        }
    }

    @Test
    fun startReceiveNotification() {
        runTest {
            val mutableSharedFlow = MutableSharedFlow<NotificationEvent>(extraBufferCapacity = 1)
            every { notification.notificationFlow() } returns mutableSharedFlow

            val job = launch(Dispatchers.Unconfined) {
                viewModel.startReceiveNotification()
            }
            advanceUntilIdle()
            mutableSharedFlow.tryEmit(mockk())
            advanceUntilIdle()

            verify { notification.notificationFlow() }
            verify { mainViewModel.sendNotification(any()) }
            job.cancel()
        }
    }

    @ParameterizedTest
    @CsvSource(
        "true",
        "false",
    )
    fun exitApplicationIfNeed(visible: Boolean) {
        every { mainViewModel.windowVisible() } returns visible
        val exitApplication = mockk<() -> Unit>()
        every { exitApplication.invoke() } just Runs

        viewModel.exitApplicationIfNeed(exitApplication)

        verify(inverse = visible) { exitApplication.invoke() }
    }

}