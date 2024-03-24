package jp.toastkid.yobidashi4.domain.service.article

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

class OozumoTemplate {

    operator fun invoke(today: LocalDate): String? {
        val monthTitle = toBashoTitle(today.month) ?: return null
        val term = term(today.year, today.monthValue)
        val dateTitle = makeDateTitle(term.indexOf(today.dayOfMonth)) ?: return null
        return """
## $monthTitle $dateTitle
${if (dateTitle == "千秋楽") THREE_SPECIAL_AWARDS else ""}
### 十両
追記

### 幕内前半戦
追記

### 幕内後半戦
追記
${if (dateTitle == "千秋楽") FINAL_DATE_PROGRAMS else ""}
""".trimIndent()
    }

    private fun makeDateTitle(dateIndex: Int): String? {
        return when (dateIndex) {
            0 -> "初日"
            1 -> "二日目"
            2 -> "三日目"
            3 -> "四日目"
            4 -> "五日目"
            5 -> "六日目"
            6 -> "七日目"
            7 -> "八日目"
            8 -> "九日目"
            9 -> "十日目"
            10 -> "十一日目"
            11 -> "十二日目"
            12 -> "十三日目"
            13 -> "十四日目"
            14 -> "千秋楽"
            else -> null
        }
    }

    private fun term(year: Int, month: Int): IntRange {
        val firstDate = calculateFirstDate(year, month)

        return (firstDate..firstDate + 14)
    }

    private fun calculateFirstDate(year: Int, month: Int): Int {
        val localDate = LocalDate.of(year, month, 1)
        val dayOfWeek = localDate.dayOfWeek
        val o = if (dayOfWeek == DayOfWeek.SUNDAY) 6 else (dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal)
        return 7 * 2 - (o)
    }

    private fun toBashoTitle(month: Month): String? {
        val monthLabel = when (month) {
            Month.JANUARY -> "一"
            Month.MARCH -> "三"
            Month.MAY -> "五"
            Month.JULY -> "七"
            Month.SEPTEMBER -> "九"
            Month.NOVEMBER -> "十一"
            else -> null
        } ?: return null
        return "大相撲${monthLabel}月場所"
    }

}

private const val THREE_SPECIAL_AWARDS = """
### 三賞

| 賞 | 力士 |
|:---|:---|
| 殊勲賞 | 
| 敢闘賞 | 
| 技能賞 | 
"""

private const val FINAL_DATE_PROGRAMS = """
### これより三役
東の方屋から追記が上がって四股を踏む。
西の方屋から追記が上がって四股を踏む。

### 表彰式
"""