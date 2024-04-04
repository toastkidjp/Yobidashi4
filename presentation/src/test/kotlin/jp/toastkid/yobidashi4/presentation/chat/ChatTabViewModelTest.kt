package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
import jp.toastkid.yobidashi4.domain.service.chat.ChatService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class ChatTabViewModelTest {

    private lateinit var subject: ChatTabViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var service: ChatService

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

        every { service.send(any()) } returns ""
        every { service.setChat(any()) } just Runs
        every { service.messages() } returns emptyList()

        subject = ChatTabViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun messages() {
        val messages = subject.messages()

        assertTrue(messages.isEmpty())
        verify { service.messages() }
    }

    @Test
    fun noopSend() {
        runBlocking {
            subject.send()

            verify(inverse = true) { service.send(any()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun send() {
        runDesktopComposeUiTest {
            setContent {
                rememberCoroutineScope().launch {
                    subject.onValueChanged(TextFieldValue("test"))

                    subject.send()

                    verify { service.send(any()) }
                }
            }
        }
    }

    @Test
    fun textInput() {
        assertTrue(subject.textInput().text.isEmpty())
    }

    @Test
    fun focusRequester() {
        assertNotNull(subject.focusRequester())
    }

    @Test
    fun launch() {
        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } just Runs

        subject.launch(mockk())

        verify { subject.focusRequester() }
        verify { service.setChat(any()) }
    }

    @Test
    fun update() {
        val chatTab = mockk<ChatTab>()
        every { mainViewModel.replaceTab(any(),  any()) } just Runs
        every { service.getChat() } returns mockk()

        subject.update(chatTab)

        verify { mainViewModel.replaceTab(any(),  any()) }
        verify { service.getChat() }
    }

    @Test
    fun label() {
        assertTrue(subject.label().isNotBlank())
    }

    @Test
    fun name() {
        assertEquals("You", subject.name("user"))
        assertEquals("Assistant", subject.name("model"))
        assertEquals("Unknown", subject.name("unknown"))
    }

    @Test
    fun scrollState() {
        assertNotNull(subject.scrollState())
    }

    @Test
    fun nameColor() {
        assertNotEquals(subject.nameColor("model"), subject.nameColor("model2"))
    }

}