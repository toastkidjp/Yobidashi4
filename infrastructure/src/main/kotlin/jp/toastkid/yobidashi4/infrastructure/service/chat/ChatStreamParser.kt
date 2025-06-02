package jp.toastkid.yobidashi4.infrastructure.service.chat

import jp.toastkid.yobidashi4.domain.repository.chat.dto.ChatResponseItem
import java.util.regex.Pattern

class ChatStreamParser {

    operator fun invoke(line: String): ChatResponseItem? {
        if (line.contains("\"finishReason\": \"SAFETY\"")) {
            return ChatResponseItem.error()
        }
        if (line.contains("\"finishReason\": \"OTHER\"")) {
            return ChatResponseItem.other()
        }

        val matcher = pattern.matcher(line)
        val message = if (matcher.find()) matcher.group(1).replace("\\n", "\n") else null

        val imageMatcher = imagePattern.matcher(line)
        val base64 = if (imageMatcher.find()) imageMatcher.group(2) else null

        val messageText = message ?: base64 ?: ""
        if (messageText.isEmpty()) {
            return null
        }

        return ChatResponseItem(
            message = messageText,
            image = base64 != null
        )
    }

}

private val pattern = Pattern.compile("\\{\"parts\": \\[\\{\"text\": \"(.+?)\"}]", Pattern.DOTALL)

private val imagePattern = Pattern.compile("\"inlineData\":(.+?)\"data\": \"(.+?)\"", Pattern.DOTALL)
