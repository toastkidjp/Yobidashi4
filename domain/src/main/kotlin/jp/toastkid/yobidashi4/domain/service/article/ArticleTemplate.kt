package jp.toastkid.yobidashi4.domain.service.article

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter

class ArticleTemplate(private val now: LocalDate = LocalDate.now(), private val offDayFinderService: OffDayFinderService) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    operator fun invoke(header: String): String {
        val inputStream = UserTemplateStreamReader().invoke() ?: return ""

        val workday = isWorkDay()
        val belt = isWeekDay()
        val market = isMarketDay()
        val stockDay = isStockDay()

        val text = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use {
            with(StringBuilder()) {
                var inWorkdayBlock = false
                var inBeltBlock = false
                var inMarketBlock = false
                var inStockBlock = false

                it.readLines().forEach { line ->
                    if (line.contains("{{title}}")) {
                        append(line.replace("{{title}}", header)).append("\n")
                        return@forEach
                    }

                    if (line.contains("{{yesterday}}")) {
                        append(line.replace("{{yesterday}}", dateFormatter.format(now.minusDays(1)))).append("\n")
                        return@forEach
                    }

                    if (line.contains("{{workday}}")) {
                        inWorkdayBlock = true
                        return@forEach
                    }

                    if (inWorkdayBlock) {
                        if (line.contains("{{/workday}}")) {
                            inWorkdayBlock = false
                            return@forEach
                        }

                        if (workday) {
                            append(line).append("\n")
                            return@forEach
                        }
                        return@forEach
                    }

                    if (line.contains("{{belt}}")) {
                        inBeltBlock = true
                        return@forEach
                    }

                    if (inBeltBlock) {
                        if (line.contains("{{/belt}}")) {
                            inBeltBlock = false
                            return@forEach
                        }

                        if (belt) {
                            append(line).append("\n")
                            return@forEach
                        }
                        return@forEach
                    }

                    if (line.contains("{{market}}")) {
                        inMarketBlock = true
                        return@forEach
                    }

                    if (inMarketBlock) {
                        if (line.contains("{{/market}}")) {
                            inMarketBlock = false
                            return@forEach
                        }

                        if (market) {
                            append(line).append("\n")
                            return@forEach
                        }
                        return@forEach
                    }

                    if (line.contains("{{stock}}")) {
                        inStockBlock = true
                        return@forEach
                    }

                    if (inStockBlock) {
                        if (line.contains("{{/stock}}")) {
                            inStockBlock = false
                            return@forEach
                        }

                        if (stockDay) {
                            append(line).append("\n")
                            return@forEach
                        }
                        return@forEach
                    }

                    if (line.contains("{{oozumo}}")) {
                        append(OozumoTemplate().invoke(now) ?: "")
                        return@forEach
                    }

                    append(line).append("\n")
                }
                toString()
            }
        }
        return text
    }

    private fun isWeekDay(): Boolean {
        val dayOfWeek = now.dayOfWeek
        return dayOfWeek in setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
    }

    private fun isWorkDay(): Boolean {
        val dayOfWeek = now.dayOfWeek
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY
                && offDayFinderService.invoke(now.year, now.monthValue, now.dayOfMonth, dayOfWeek).not()
    }

    private fun isMarketDay(): Boolean {
        val dayOfWeek = now.dayOfWeek
        if (JapaneseStockMarketCloseDaysFinder().invoke(now.year, now.month, now.dayOfMonth)) {
            return false
        }
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY
                && offDayFinderService.invoke(now.year, now.monthValue, now.dayOfMonth, dayOfWeek, false).not()
    }

    private fun isStockDay(): Boolean {
        val dayOfWeek = now.dayOfWeek
        if (now.month == Month.JANUARY && now.dayOfMonth <= 3) {
            return false
        }
        return dayOfWeek != DayOfWeek.MONDAY && dayOfWeek != DayOfWeek.SUNDAY
    }

}