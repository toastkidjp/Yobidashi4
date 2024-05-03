package jp.toastkid.yobidashi4.presentation.tool.notification

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import jp.toastkid.yobidashi4.domain.repository.notification.NotificationEventRepository
import jp.toastkid.yobidashi4.presentation.tool.notification.viewmodel.NotificationListTabViewModel
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class NotificationListTabViewKtTest {

    @MockK
    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var repository: NotificationEventRepository

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single(qualifier = null) { viewModel } bind(MainViewModel::class)
                    single(qualifier = null) { repository } bind(NotificationEventRepository::class)
                }
            )
        }

        MockKAnnotations.init(this)

        mockkConstructor(NotificationListTabViewModel::class)
        every { anyConstructed<NotificationListTabViewModel>().listState() } returns LazyListState()
        every { anyConstructed<NotificationListTabViewModel>().start(Dispatchers.IO) } just Runs
        every { anyConstructed<NotificationListTabViewModel>().items() } returns mutableListOf(NotificationEvent.makeDefault())
        every { anyConstructed<NotificationListTabViewModel>().focusRequester() } returns FocusRequester()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun notificationListTabView() {
        runDesktopComposeUiTest {
            setContent {
                NotificationListTabView()
            }
        }
    }
}