package jp.toastkid.yobidashi4.domain.service.converter

import java.time.DateTimeException
import java.time.LocalDate
import java.time.chrono.JapaneseDate
import java.time.chrono.JapaneseEra
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class JapaneseAgeConverterService : TwoStringConverterService {

    override fun title(): String {
        return "Japanese Age"
    }

    override fun firstInputLabel(): String {
        return "Japanese born year(和暦)"
    }

    override fun secondInputLabel(): String {
        return "Age"
    }

    override fun defaultFirstInputValue(): String {
        return "平成3年"
    }

    override fun defaultSecondInputValue(): String {
        return "${firstInputAction(defaultFirstInputValue())}"
    }

    override fun firstInputAction(input: String): String? {
        if (input.length < 3) {
            return null
        }
        val eraString = input.substring(0, 2)
        val yearOfEra = input.substring(2).toIntOrNull() ?: return null
        val era = when (eraString) {
            "明治" -> JapaneseEra.MEIJI
            "大正" -> JapaneseEra.TAISHO
            "昭和" -> JapaneseEra.SHOWA
            "平成" -> JapaneseEra.HEISEI
            "令和" -> JapaneseEra.REIWA
            else -> null
        } ?: return null

        if (era == JapaneseEra.MEIJI && yearOfEra < 6) {
            return null
        }

        val japaneseDate = try {
            JapaneseDate.of(era, yearOfEra, 12, 26)
        } catch (e: DateTimeException) {
            return null
        }

        val ofEpochDay = LocalDate.ofEpochDay(japaneseDate.toEpochDay())

        return (ofEpochDay.withYear(LocalDate.now().year).year - ofEpochDay.year).toString()
    }

    override fun secondInputAction(input: String): String? {
        val age = input.toIntOrNull() ?: return null
        val japaneseBirthDate = try {
            JapaneseDate.now().minus(age.toLong(), ChronoUnit.YEARS)
        } catch (e: DateTimeException) {
            return null
        }
        return japaneseBirthDate.format(DateTimeFormatter.ofPattern("Gy"))
    }

}