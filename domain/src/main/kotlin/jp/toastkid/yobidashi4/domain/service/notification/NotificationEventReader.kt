package jp.toastkid.yobidashi4.domain.service.notification

import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent

/**
 * <pre>
 * Test	This notification is only attempt to send.	2023-12-08 10:51:00
 * Test2	This notification is only attempt to send.	2023-12-08 10:52:00
 * Test3	This notification is only attempt to send.	2023-12-08 10:49:00
 * </pre>
 */
class NotificationEventReader {

    private val path = Path.of("user/notification/list.tsv")

    operator fun invoke(): List<NotificationEvent> {
        if (Files.exists(path).not()) {
            return emptyList()
        }

        return Files.readAllLines(path).filter { it.contains(DELIMITER) }
            .mapNotNull {
                val split = it.split(DELIMITER)
                if (split.size < 3 || split[2].isBlank()) {
                    return@mapNotNull null
                }

                val date = LocalDateTime.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse(split[2]))
                NotificationEvent(
                    split[0],
                    split[1],
                    date
                )
            }
    }

}

private const val DELIMITER = "\t"
