/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.repository.notification

import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import jp.toastkid.yobidashi4.domain.model.web.history.DELIMITER
import jp.toastkid.yobidashi4.domain.repository.notification.NotificationEventRepository
import org.koin.core.annotation.Single
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

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
        if (Files.exists(path.parent).not()) {
            Files.createDirectories(path.parent)
        }
        if (Files.exists(path).not()) {
            Files.createFile(path)
        }
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
        writeToFile(toMutableList.map(NotificationEvent::toTsv))
    }

    override fun deleteAt(index: Int) {
        writeToFile(readAll().filterIndexed { i, _ -> i != index }.map(NotificationEvent::toTsv))
    }

    private fun writeToFile(content: Iterable<String>) {
        Files.write(path, content)
    }

    override fun clear() {
        Files.write(path, byteArrayOf())
    }

}
