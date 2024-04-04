package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.chat.ChatMessage
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
import jp.toastkid.yobidashi4.domain.service.chat.ChatService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class ChatTabViewKtTest {

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var service: ChatService

    @MockK
    private lateinit var tab: ChatTab

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier=null) { service } bind(ChatService::class)
                }
            )
        }

        mockkConstructor(ChatTabViewModel::class)

        every { anyConstructed<ChatTabViewModel>().scrollState() } returns LazyListState(0)
        every { anyConstructed<ChatTabViewModel>().messages() } returns listOf(
            ChatMessage("user", "test"),
            ChatMessage("model", "test")
        )
        every { anyConstructed<ChatTabViewModel>().launch(any()) } just Runs
        every { anyConstructed<ChatTabViewModel>().update(any()) } just Runs
        every { tab.chat() } returns mockk()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun chatTabView() {
        runDesktopComposeUiTest {
            setContent {
                ChatTabView(tab)
            }
        }
    }

}