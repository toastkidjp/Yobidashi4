package jp.toastkid.yobidashi4.domain.service.chat

import jp.toastkid.yobidashi4.domain.model.chat.Chat
import jp.toastkid.yobidashi4.domain.model.chat.ChatMessage
import jp.toastkid.yobidashi4.domain.repository.chat.dto.ChatResponseItem

interface ChatService {

    fun send(messages: MutableList<ChatMessage>, image: Boolean, onUpdate: (ChatResponseItem?) -> Unit): String?

    fun setChat(chat: Chat)

    fun getChat(): Chat

    fun messages(): List<ChatMessage>

}