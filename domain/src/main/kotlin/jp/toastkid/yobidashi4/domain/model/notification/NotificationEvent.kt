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
            } catch (e: DateTimeParseException) {
                null
            }
        }

        fun makeDefault() = NotificationEvent("New", "New notification's message", LocalDateTime.now())

    }

}

private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")