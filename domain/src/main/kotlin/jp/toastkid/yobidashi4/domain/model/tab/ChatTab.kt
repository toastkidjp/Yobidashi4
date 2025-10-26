package jp.toastkid.yobidashi4.domain.model.tab

import jp.toastkid.yobidashi4.domain.model.chat.Chat
import jp.toastkid.yobidashi4.domain.model.chat.GenerativeAiModel

class ChatTab(
    private val chat: Chat = Chat(),
    private val scrollPosition: Int = 0,
    private val initialModel: GenerativeAiModel = GenerativeAiModel.default(),
    private val initialQuestion: String = "",
) : ScrollableContentTab {

    fun chat() = chat

    override fun title(): String {
        return "Chat"
    }

    override fun scrollPosition() = scrollPosition

    override fun withNewPosition(scrollPosition: Int): ScrollableContentTab {
        return ChatTab(this.chat, scrollPosition)
    }

    fun shouldSendInitialQuestion(): Boolean {
        return initialQuestion.isNotEmpty()
    }

    fun initialModel() = initialModel

    fun initialQuestion() = initialQuestion

}