package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.TextFieldValue
import jp.toastkid.yobidashi4.domain.model.chat.Chat
import jp.toastkid.yobidashi4.domain.model.chat.ChatMessage
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
import jp.toastkid.yobidashi4.domain.service.chat.ChatService
import jp.toastkid.yobidashi4.domain.service.io.IoContextProvider
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.max

class ChatTabViewModel : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    private val service: ChatService by inject()

    private val textInput = mutableStateOf(TextFieldValue())

    private val focusRequester = FocusRequester()

    private val scrollState = LazyListState()

    private val clipboardPutterService = ClipboardPutterService()

    private val ioContextProvider: IoContextProvider by inject()

    fun messages(): List<ChatMessage> = service.messages()

    private val useImageGeneration = mutableStateOf(false)

    fun useImageGeneration() = useImageGeneration.value

    fun setImageGeneration(newState: Boolean) {
        useImageGeneration.value = newState
    }

    suspend fun send(coroutineScope: CoroutineScope) {
        val text = textInput.value.text
        if (text.isBlank()) {
            return
        }

        textInput.value = TextFieldValue()

        labelState.value = "Connecting in progress..."
        withContext(ioContextProvider()) {
            service.send(text, useImageGeneration.value) {
                coroutineScope.launch {
                    scrollState.animateScrollToItem(scrollState.layoutInfo.totalItemsCount)
                }
            }
        }
        labelState.value = DEFAULT_LABEL
    }

    fun onChatListKeyEvent(coroutineScope: CoroutineScope, keyEvent: KeyEvent): Boolean {
        if (keyEvent.type != KeyEventType.KeyDown) {
            return false
        }

        return when {
            keyEvent.key == Key.DirectionUp && keyEvent.isCtrlPressed -> {
                coroutineScope.launch {
                    scrollState.scrollToItem(0, 0)
                }
                return true
            }
            keyEvent.key == Key.DirectionDown && keyEvent.isCtrlPressed -> {
                coroutineScope.launch {
                    scrollState.scrollToItem(scrollState.layoutInfo.totalItemsCount - 1, 0)
                }
                return true
            }
            keyEvent.key == Key.DirectionUp -> {
                coroutineScope.launch {
                    scrollState.scrollToItem(max(0, scrollState.firstVisibleItemIndex - 1), 0)
                }
                return true
            }
            keyEvent.key == Key.DirectionDown -> {
                coroutineScope.launch {
                    scrollState.scrollToItem(kotlin.math.min(scrollState.layoutInfo.totalItemsCount - 1, scrollState.firstVisibleItemIndex + 1), 0)
                }
                return true
            }
            else -> false
        }
    }

    fun textInput() = textInput.value

    fun onValueChanged(newValue: TextFieldValue) {
        textInput.value = newValue
    }

    fun focusRequester(): FocusRequester {
        return focusRequester
    }

    fun launch(chat: Chat) {
        service.setChat(chat)
        focusRequester().requestFocus()
    }

    fun update(chatTab: ChatTab) {
        mainViewModel.replaceTab(chatTab,  ChatTab(service.getChat()))
    }

    private val labelState = mutableStateOf(DEFAULT_LABEL)

    fun label(): String {
        return labelState.value
    }

    fun name(role: String): String {
        return when (role) {
            "user" -> "You"
            "model" -> "Assistant"
            else -> "Unknown"
        }
    }

    fun scrollState(): LazyListState {
        return scrollState
    }

    fun onKeyEvent(coroutineScope: CoroutineScope, it: KeyEvent): Boolean {
        if (textInput().composition == null && it.isCtrlPressed && it.key == Key.Enter) {
            coroutineScope.launch {
                send(coroutineScope)
            }
            return true
        }

        return false
    }

    fun nameColor(role: String): Color {
        return Color(if (role == "model") 0xFF86EEC7 else 0xFFFFD54F)
    }

    fun clipText(text: String) {
        clipboardPutterService.invoke(text)

        mainViewModel.showSnackbar("Clipped text.")
    }

}

private const val DEFAULT_LABEL = "Please would you input any sentences which you know something?"
