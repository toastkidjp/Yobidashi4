package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.text.input.TextFieldValue
import jp.toastkid.yobidashi4.domain.model.chat.Chat
import jp.toastkid.yobidashi4.domain.model.chat.ChatMessage
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
import jp.toastkid.yobidashi4.domain.service.chat.ChatService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatTabViewModel : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    private val service: ChatService by inject()

    private val textInput = mutableStateOf(TextFieldValue())

    private val focusRequester = FocusRequester()

    private val scrollState = LazyListState()

    fun messages(): List<ChatMessage> = service.messages()

    suspend fun send() {
        val text = textInput.value.text
        if (text.isBlank()) {
            return
        }

        textInput.value = TextFieldValue()

        labelState.value = "Connecting in progress..."
        withContext(Dispatchers.IO) {
            service.send(text)
        }
        labelState.value = DEFAULT_LABEL
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
                send()
            }
            return true
        }

        return false
    }

    fun nameColor(role: String): Color {
        return Color(if (role == "model") 0xFF86EEC7 else 0xFFFFD54F)
    }

}

private const val DEFAULT_LABEL = "Please would you input any sentences which you know something?"
