package jp.toastkid.yobidashi4.presentation.chat

import jp.toastkid.yobidashi4.domain.model.chat.GenerativeAiModel
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_chat
import jp.toastkid.yobidashi4.library.resources.ic_image
import org.jetbrains.compose.resources.DrawableResource

class ChatModelIconMapper {

    operator fun invoke(model: GenerativeAiModel): DrawableResource {
        return when (model) {
            GenerativeAiModel.GEMINI_2_0_FLASH_IMAGE -> Res.drawable.ic_image
            else -> Res.drawable.ic_chat
        }
    }

}