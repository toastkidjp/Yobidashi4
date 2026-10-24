/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.notification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

data class NotificationEvent(
    val title: String,
    val text: String,
    val date: LocalDateTime
) {

    fun dateTimeString(): String = date.format(DATE_TIME_FORMATTER)

    fun toTsv() = "${title}\t${text}\t${dateTimeString()}"

    companion object {
        fun parse(text: String): LocalDateTime? {
            return try {
                LocalDateTime.from(DATE_TIME_FORMATTER.parse(text))
            } catch (_: DateTimeParseException) {
                null
            }
        }

        fun makeDefault() =
            NotificationEvent("New", "New notification's message", LocalDateTime.now())

    }

}

private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")