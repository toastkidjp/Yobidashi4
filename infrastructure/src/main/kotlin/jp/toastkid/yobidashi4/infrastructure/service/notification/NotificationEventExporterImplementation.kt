/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.notification

import jp.toastkid.yobidashi4.domain.repository.notification.NotificationEventRepository
import jp.toastkid.yobidashi4.domain.service.notification.NotificationEventExporter
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Single
class NotificationEventExporterImplementation(
    private val fileSystem: FileSystem
) : NotificationEventExporter, KoinComponent {

    private val currentTimeSuffixDateTimeFormatter = DateTimeFormatter.ofPattern("_yyyyMMdd_HHmmss")

    private val repo: NotificationEventRepository by inject()

    override fun invoke() {
        val path = "notification${
            makeCurrentTimeSuffix()
        }.tsv".toPath()
        fileSystem.write(path) {
            writeUtf8(repo.readAll().joinToString(System.lineSeparator()) { it.toTsv() })
        }
    }

    private fun makeCurrentTimeSuffix(): String =
        LocalDateTime.now().format(currentTimeSuffixDateTimeFormatter)

}
