package jp.toastkid.yobidashi4.domain.service.article

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.time.DayOfWeek
import java.time.LocalDate

class ArticleTemplate {
    operator fun invoke(header: String): String {
        val userTemplatePath = Paths.get("user/article_template.txt")
        val inputStream = if (Files.exists(userTemplatePath)) {
            Files.newInputStream(userTemplatePath)
        } else {
            javaClass.classLoader?.getResourceAsStream("article/template/article_template.txt")
        } ?: return ""
        val text = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use {
            with(StringBuilder()) {
                var inWorkdayBlock = false
                var inBeltBlock = false
                var inMarketBlock = false
                var inStockBlock = false
                val workday = isWorkDay()
                val belt = isWeekDay()
                val market = isMarketDay()
                val stockDay = isStockDay()

                it.readLines().forEach { line ->
                    if (line.contains("{{title}}")) {
                        append(line.replace("{{title}}", header)).append("\n")
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

                    append(line).append("\n")
                }
                toString()
            }
        }
        return text
    }

    private fun isWeekDay(): Boolean {
        val now = LocalDate.now()
        val dayOfWeek = now.dayOfWeek
        return dayOfWeek in setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
    }

    private fun isWorkDay(): Boolean {
        val now = LocalDate.now()
        val dayOfWeek = now.dayOfWeek
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY
                && OffDayFinderService().invoke(now.year, now.monthValue, now.dayOfMonth, dayOfWeek).not()
    }

    private fun isMarketDay(): Boolean {
        val now = LocalDate.now()
        val dayOfWeek = now.dayOfWeek
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY
                && OffDayFinderService().invoke(now.year, now.monthValue, now.dayOfMonth, dayOfWeek, false).not()
    }

    private fun isStockDay(): Boolean {
        val now = LocalDate.now()
        val dayOfWeek = now.dayOfWeek
        return dayOfWeek != DayOfWeek.MONDAY && dayOfWeek != DayOfWeek.SUNDAY
    }

}