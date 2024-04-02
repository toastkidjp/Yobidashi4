package jp.toastkid.yobidashi4.domain.service.chat

import jp.toastkid.yobidashi4.domain.model.chat.Chat
import jp.toastkid.yobidashi4.domain.model.chat.ChatMessage

interface ChatService {

    fun send(text: String): String?

    fun setChat(chat: Chat)

    fun getChat(): Chat

    fun messages(): List<ChatMessage>

}