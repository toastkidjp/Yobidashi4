package jp.toastkid.yobidashi4.infrastructure.service.chat

import java.util.regex.Pattern

class ChatStreamParser {

    operator fun invoke(line: String): String? {
        if (line.contains("\"finishReason\": \"SAFETY\"")) {
            return "[ERROR]"
        }
        if (line.contains("\"finishReason\": \"OTHER\"")) {
            return "[OTHER]"
        }

        val matcher = pattern.matcher(line)
        return if (matcher.find()) matcher.group(1).replace("\\n", "\n") else null
    }

}

private val pattern = Pattern.compile("\\{\"parts\": \\[\\{\"text\": \"(.+?)\"}]", Pattern.DOTALL)
