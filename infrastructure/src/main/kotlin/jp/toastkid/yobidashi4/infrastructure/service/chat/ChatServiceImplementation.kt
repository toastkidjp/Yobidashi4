/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.chat

import jp.toastkid.yobidashi4.domain.model.chat.Chat
import jp.toastkid.yobidashi4.domain.model.chat.ChatMessage
import jp.toastkid.yobidashi4.domain.model.chat.GenerativeAiModel
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.repository.chat.ChatRepository
import jp.toastkid.yobidashi4.domain.repository.chat.dto.ChatResponseItem
import jp.toastkid.yobidashi4.domain.service.chat.ChatService
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.ParametersHolder
import java.util.concurrent.atomic.AtomicReference

@Single
class ChatServiceImplementation : ChatService, KoinComponent {

    private val chatHolder: AtomicReference<Chat> = AtomicReference(Chat())

    private val setting: Setting by inject()

    private fun loadRepositories(): Map<GenerativeAiModel, ChatRepository> {
        return GenerativeAiModel.entries.map { model ->
            val repository: ChatRepository by inject(parameters = {
                ParametersHolder(mutableListOf(setting.chatApiKey(), model.url()))
            })
            return@map model to repository
        }.toMap<GenerativeAiModel, ChatRepository>()
    }

    private val repositories = loadRepositories()

    override fun send(
        messages: MutableList<ChatMessage>,
        model: GenerativeAiModel,
        onUpdate: (ChatResponseItem?) -> Unit
    ): String? {
        val chat = Chat(messages)

        val repository = repositories.get(model) ?: return null
        repository.request(chat.makeContent(model.image()), onUpdate)

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