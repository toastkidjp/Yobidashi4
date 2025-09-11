/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.chat

import jp.toastkid.yobidashi4.domain.model.chat.Chat
import jp.toastkid.yobidashi4.domain.model.chat.ChatMessage
import jp.toastkid.yobidashi4.domain.model.chat.GenerativeAiModel
import jp.toastkid.yobidashi4.domain.repository.chat.dto.ChatResponseItem

interface ChatService {

    fun send(
        messages: MutableList<ChatMessage>,
        model: GenerativeAiModel,
        onUpdate: (ChatResponseItem?) -> Unit
    ): String?

    fun setChat(chat: Chat)

    fun getChat(): Chat

    fun messages(): List<ChatMessage>

}