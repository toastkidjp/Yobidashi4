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

    override fun defaultFirstInputValue(): String = LocalDateTime.now().toInstant(offset).toEpochMilli().toString()

    override fun defaultSecondInputValue(): String = LocalDateTime.now().format(dateFormatter).toString()

    override  fun firstInputAction(input: String): String? {
        return try {
            LocalDateTime
                .ofInstant(Instant.ofEpochMilli(input.toLong()), ZoneId.systemDefault())
                .format(dateFormatter)
        } catch (e: Exception) {
            null
        }
    }

    override fun secondInputAction(input: String): String? {
        return try {
            LocalDateTime.parse(input, dateFormatter)
                .toInstant(offset)
                .toEpochMilli()
                .toString()
        } catch (e: DateTimeException) {
            // > /dev/null
            null
        }
    }
}