package jp.toastkid.yobidashi4.domain.model.tab

import jp.toastkid.yobidashi4.domain.model.chat.Chat

class ChatTab(
    private val chat: Chat = Chat(),
    private val scrollPosition: Int = 0,
) : ScrollableContentTab {

    fun chat() = chat

    override fun title(): String {
        return "Chat"
    }

    override fun scrollPosition() = scrollPosition

    override fun withNewPosition(scrollPosition: Int): ScrollableContentTab {
        return ChatTab(this.chat, scrollPosition)
    }

}