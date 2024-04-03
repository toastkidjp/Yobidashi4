package jp.toastkid.yobidashi4.domain.model.tab

import jp.toastkid.yobidashi4.domain.model.chat.Chat

class ChatTab(private val chat: Chat = Chat()) : Tab {

    fun chat() = chat

    override fun title(): String {
        return "Chat"
    }

    override fun iconPath(): String? {
        return "images/icon/ic_chat.xml"
    }

}