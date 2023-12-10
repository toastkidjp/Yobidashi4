package jp.toastkid.yobidashi4.infrastructure.repository.notification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import jp.toastkid.yobidashi4.domain.model.web.history.DELIMITER
import jp.toastkid.yobidashi4.domain.repository.notification.NotificationEventRepository
import org.koin.core.annotation.Single

/**
 * <pre>
 * Test	This notification is only attempt to send.	2023-12-08 10:51:00
 * Test2	This notification is only attempt to send.	2023-12-08 10:52:00
 * Test3	This notification is only attempt to send.	2023-12-08 10:49:00
 * </pre>
 */
@Single
class NotificationEventFileStore : NotificationEventRepository {

    private val path = Path.of("user/notification/list.tsv")

    override fun add(event: NotificationEvent) {
        Files.write(path, event.toTsv().toByteArray(), StandardOpenOption.APPEND)
    }

    override fun readAll(): List<NotificationEvent> {
        if (Files.exists(path).not()) {
            return emptyList()
        }

        return Files.readAllLines(path).filter { it.contains(DELIMITER) }
            .mapNotNull {
                val split = it.split(DELIMITER)
                if (split.size < 3 || split[2].isBlank()) {
                    return@mapNotNull null
                }

                val date = NotificationEvent.parse(split[2]) ?: return@mapNotNull null
                NotificationEvent(
                    split[0],
                    split[1],
                    date
                )
            }
    }

    override fun update(index: Int, event: NotificationEvent) {
        val readAll = readAll()
        if (index < 0 || index >= readAll.size) {
            return
        }

        val toMutableList = readAll.toMutableList()
        toMutableList[index] = event
        Files.write(
            path,
            toMutableList.map { it.toTsv() }
        )
    }

    override fun deleteAt(index: Int) {
        Files.write(
            path,
            readAll().filterIndexed { i, _ -> i != index }.map { it.toTsv() }
        )
    }

    override fun clear() {
        Files.write(path, byteArrayOf())
    }

}

private const val DELIMITER = "\t"
