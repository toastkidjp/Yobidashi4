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
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import org.koin.core.annotation.Single

/**
 * <pre>
 * Test	This notification is only attempt to send.	2023-12-08 10:51:00
 * Test2	This notification is only attempt to send.	2023-12-08 10:52:00
 * Test3	This notification is only attempt to send.	2023-12-08 10:49:00
 * </pre>
 */
@Single
class NotificationEventFileStore(private val  fileSystem: FileSystem) : NotificationEventRepository {

    private val path = "user/notification/list.tsv".toPath()

    override fun add(event: NotificationEvent) {
        val parent = path.parent ?: return
        if (fileSystem.exists(parent).not()) {
            fileSystem.createDirectories(parent)
        }
        if (fileSystem.exists(path).not()) {
            fileSystem.write(path) {}
        }
        fileSystem.appendingSink(path).buffer().use {
            it.writeUtf8("\n" + event.toTsv())
        }
    }

    override fun readAll(): List<NotificationEvent> {
        if (fileSystem.exists(path).not()) {
            return emptyList()
        }

        return fileSystem.source(path).buffer().use {
            it.readUtf8().trim().split("\n")
        }.filter { it.contains(DELIMITER) }
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
        fileSystem.write(path) {
            writeUtf8(content.joinToString("\n"))
        }
    }

    override fun clear() {
        fileSystem.delete(path)
    }

}
