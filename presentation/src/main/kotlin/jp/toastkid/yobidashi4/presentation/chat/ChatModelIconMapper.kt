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