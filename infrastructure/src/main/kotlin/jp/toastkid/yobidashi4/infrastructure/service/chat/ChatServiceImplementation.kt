package jp.toastkid.yobidashi4.infrastructure.service.chat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import jp.toastkid.yobidashi4.domain.model.chat.Chat
import jp.toastkid.yobidashi4.domain.model.chat.ChatMessage
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.repository.chat.ChatRepository
import jp.toastkid.yobidashi4.domain.service.chat.ChatService
import jp.toastkid.yobidashi4.infrastructure.model.chat.CHAT
import jp.toastkid.yobidashi4.infrastructure.model.chat.IMAGE_GENERATOR
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.ParametersHolder

@Single
class ChatServiceImplementation : ChatService, KoinComponent {

    private val chatHolder: MutableState<Chat> = mutableStateOf(Chat())

    private val setting: Setting by inject()

    private val repository: ChatRepository by inject(parameters = {
        ParametersHolder(mutableListOf(setting.chatApiKey(), CHAT))
    })

    private val imageGeneratorRepository: ChatRepository by inject(parameters = {
        ParametersHolder(mutableListOf(setting.chatApiKey(), IMAGE_GENERATOR))
    })

    override fun send(text: String, image: Boolean, onUpdate: () -> Unit): String? {
        val chat = chatHolder.value
        chat.addUserText(text)
        onUpdate()

        (if (image) imageGeneratorRepository else repository)
            .request(chat.makeContent(image)) {
            if (it == null) {
                return@request
            }

            if (it.image()) {
                chat.addModelImage(it.message())
            } else {
                chat.addModelText(it.message().replace("\"", ""))
            }

            onUpdate()
        }

        return null
    }

    override fun setChat(chat: Chat) {
        chatHolder.value = (chat)
    }

    override fun getChat(): Chat {
        return chatHolder.value
    }

    override fun messages(): List<ChatMessage> {
        return chatHolder.value.list()
    }

    override fun clearMessages() {
        chatHolder.value.clearMessages()
    }

}