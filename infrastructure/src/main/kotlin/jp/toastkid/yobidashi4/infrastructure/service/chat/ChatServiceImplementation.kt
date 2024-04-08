package jp.toastkid.yobidashi4.infrastructure.service.chat

import java.util.concurrent.atomic.AtomicReference
import jp.toastkid.yobidashi4.domain.model.chat.Chat
import jp.toastkid.yobidashi4.domain.model.chat.ChatMessage
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.repository.chat.ChatRepository
import jp.toastkid.yobidashi4.domain.service.chat.ChatService
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.ParametersHolder

@Single
class ChatServiceImplementation : ChatService, KoinComponent {

    private val chatHolder: AtomicReference<Chat> = AtomicReference(Chat())

    private val setting: Setting by inject()

    private val repository: ChatRepository by inject(parameters = { ParametersHolder(mutableListOf(setting.chatApiKey())) })

    override fun send(text: String): String? {
        val chat = chatHolder.get()
        chat.addUserText(text)

        //val responses = mutableListOf<String>()
        repository.request(chat.makeContent(), {
            if (it != null) {
                chat.addModelText(it)
                //responses.add(it)
                return@request
            }

            chat.removeLastUserText()
        })
        //chat.addModelTexts(responses)

        return null
    }

    override fun setChat(chat: Chat) {
        chatHolder.set(chat)
    }

    override fun getChat(): Chat {
        return chatHolder.get()
    }

    override fun messages(): List<ChatMessage> {
        return chatHolder.get().list()
    }

}