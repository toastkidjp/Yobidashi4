package jp.toastkid.yobidashi4.domain.repository.chat

import jp.toastkid.yobidashi4.domain.model.chat.Chat

interface ChatExporter {

    operator fun invoke(chat: Chat)

}