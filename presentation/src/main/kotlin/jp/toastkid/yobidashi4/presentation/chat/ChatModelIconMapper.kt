/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.chat

import jp.toastkid.yobidashi4.domain.model.chat.GenerativeAiModel
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_chat
import org.jetbrains.compose.resources.DrawableResource

class ChatModelIconMapper {

    operator fun invoke(model: GenerativeAiModel): DrawableResource {
        return when (model) {
            else -> Res.drawable.ic_chat
        }
    }

}