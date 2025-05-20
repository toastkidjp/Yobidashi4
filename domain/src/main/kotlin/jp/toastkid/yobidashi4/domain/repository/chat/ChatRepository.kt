package jp.toastkid.yobidashi4.domain.repository.chat

import jp.toastkid.yobidashi4.domain.repository.chat.dto.ChatResponseItem

interface ChatRepository {

    fun request(content: String, streamLineConsumer: (ChatResponseItem?) -> Unit)

}