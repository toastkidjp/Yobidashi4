/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.converter

import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class UnixTimeConverterService : TwoStringConverterService {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private val offset = OffsetDateTime.now().offset

    override fun title(): String {
        return "Unixtime Converter"
    }

    override fun firstInputLabel(): String {
        return "Unixtime"
    }

    override fun secondInputLabel(): String {
        return "Date time"
    }

    override fun defaultFirstInputValue(): String =
        LocalDateTime.now().toInstant(offset).toEpochMilli().toString()

    override fun defaultSecondInputValue(): String =
        LocalDateTime.now().format(dateFormatter).toString()

    override  fun firstInputAction(input: String): String? {
        return try {
            LocalDateTime
                .ofInstant(Instant.ofEpochMilli(input.toLong()), ZoneId.systemDefault())
                .format(dateFormatter)
        } catch (_: Exception) {
            null
        }
    }

    override fun secondInputAction(input: String): String? {
        return try {
            LocalDateTime.parse(input, dateFormatter)
                .toInstant(offset)
                .toEpochMilli()
                .toString()
        } catch (_: DateTimeException) {
            // > /dev/null
            null
        }
    }
}