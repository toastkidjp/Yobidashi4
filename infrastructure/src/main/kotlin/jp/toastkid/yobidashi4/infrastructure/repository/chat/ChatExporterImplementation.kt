package jp.toastkid.yobidashi4.infrastructure.repository.chat

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import jp.toastkid.yobidashi4.domain.model.chat.Chat
import jp.toastkid.yobidashi4.domain.repository.chat.ChatExporter
import org.koin.core.annotation.Single

@Single
class ChatExporterImplementation : ChatExporter {

    override fun invoke(chat: Chat) {
        val folder = Path.of("user/chat")

        if (Files.exists(folder).not()) {
            Files.createDirectories(folder)
        }

        Files.write(
            folder.resolve("chat_${DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())}.json"),
            chat.makeContent().toByteArray(StandardCharsets.UTF_8)
        )
    }

}