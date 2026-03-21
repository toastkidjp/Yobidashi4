package jp.toastkid.yobidashi4.infrastructure.service.chat

import jp.toastkid.yobidashi4.domain.model.chat.Source
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
        val message =
            if (matcher.find())
                matcher.group(1)
                    .replace("\\n", "\n")
                    .replace("\\u003e", ">")
                    .replace("\\u003c", "<")
            else
                null

        val sources = mutableListOf<Source>()
        if (line.contains("groundingChunks")) {
            val sourceMatcher = sourcePattern.matcher(line)
            while (sourceMatcher.find()) {
                sources.add(
                    Source(
                        sourceMatcher.group(3),
                        sourceMatcher.group(2)
                    )
                )
            }
        }

        val imageMatcher = imagePattern.matcher(line)
        val base64 = if (imageMatcher.find()) imageMatcher.group(2) else null

        val messageText = message ?: base64 ?: ""
        if (messageText.isEmpty()) {
            return null
        }

        return ChatResponseItem(
            message = messageText,
            image = base64 != null,
            sources = sources
        )
    }

}

private val pattern = Pattern.compile("\\{\"parts\": \\[\\{\"text\": \"(.*?)\"}]", Pattern.DOTALL)

private val imagePattern = Pattern.compile("\"inlineData\":(.+?)\"data\": \"(.+?)\"", Pattern.DOTALL)

private val sourcePattern = Pattern.compile("\\{\"(web|maps)\": \\{\"uri\": \"(.+?)\",\"title\": \"(.+?)\"", Pattern.DOTALL)
