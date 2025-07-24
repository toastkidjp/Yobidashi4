package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
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
import jp.toastkid.yobidashi4.domain.repository.chat.dto.ChatResponseItem
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

    private val messages = mutableStateListOf<ChatMessage>()

    fun messages(): List<ChatMessage> = messages

    private val useImageGeneration = mutableStateOf(false)

    fun useImageGeneration() = useImageGeneration.value

    fun setImageGeneration(newState: Boolean) {
        useImageGeneration.value = newState
    }

    fun switchImageGeneration() {
        useImageGeneration.value = useImageGeneration.value.not()
    }

    suspend fun send(coroutineScope: CoroutineScope) {
        val text = textInput.value.text
        if (text.isBlank()) {
            return
        }

        textInput.value = TextFieldValue()
        messages.add(
            ChatMessage(
                "user",
                if (useImageGeneration.value) "$text\n画像は著作権及び肖像権に問題のない形で出力してください。画像に文字を入れてはいけません。文字が入っていることを確認したら罰金\$100を科します"
                else text
            )
        )
        coroutineScope.launch {
            scrollState.animateScrollToItem(scrollState.layoutInfo.totalItemsCount)
        }

        labelState.value = "Connecting in progress..."
        withContext(ioContextProvider()) {
            service.send(messages, useImageGeneration.value) {
                onReceive(it)
                coroutineScope.launch {
                    scrollState.animateScrollToItem(scrollState.layoutInfo.totalItemsCount)
                }
            }
        }
        labelState.value = DEFAULT_LABEL
    }

    private fun onReceive(item: ChatResponseItem?) {
        if (item == null) {
            return
        }

        if (item.image()) {
            addImage(item)
            return
        }

        addText(item)
    }

    private fun addImage(item: ChatResponseItem) {
        if (messages.isEmpty() || messages.last().role != "model") {
            messages.add(ChatMessage("model", text = "", image = item.message()))
            return
        }

        val element = messages.last()
        messages.set(messages.lastIndex, element.copy(image = item.message()))
    }

    private fun addText(item: ChatResponseItem) {
        val newText = item.message().replace("\"", "")
        if (messages.isEmpty() || messages.last().role != "model") {
            messages.add(ChatMessage("model", newText))
            return
        }

        val element = messages.last()
        messages.set(messages.lastIndex, element.copy(text = element.text + newText))
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

    suspend fun launch(chat: Chat, scrollPosition: Int) {
        messages.clear()
        messages.addAll(chat.list())
        service.setChat(chat)
        if (scrollState.firstVisibleItemScrollOffset != scrollPosition) {
            scrollState.scrollToItem(scrollPosition)
        }
        focusRequester().requestFocus()
    }

    fun update(chatTab: ChatTab) {
        mainViewModel.replaceTab(
            chatTab,
            ChatTab(
                Chat(messages.toMutableList()),
                scrollState.firstVisibleItemScrollOffset
            )
        )
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

    fun clearChat() {
        messages.clear()
    }

}

private const val DEFAULT_LABEL = "Please would you input any sentences which you know something?"
