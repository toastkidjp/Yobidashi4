package jp.toastkid.yobidashi4.domain.service.converter

import java.time.DateTimeException
import java.time.chrono.JapaneseDate
import java.time.chrono.JapaneseEra
import java.time.format.DateTimeFormatter

class JapaneseEraConverterService : TwoStringConverterService {

    override fun title(): String {
        return "Japanese Era converter"
    }

    override fun firstInputLabel(): String {
        return "Japanese Era year(和暦)"
    }

    override fun secondInputLabel(): String {
        return "A.D.(西暦)"
    }

    override fun defaultFirstInputValue(): String {
        return "平成31年"
    }

    override fun defaultSecondInputValue(): String {
        return "2019年"
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
            JapaneseDate.of(era, yearOfEra, 12, 28)
        } catch (e: DateTimeException) {
            return null
        }
        return japaneseDate.format(DateTimeFormatter.ofPattern("Y"))
    }

    override fun secondInputAction(input: String): String? {
        val year = input.toIntOrNull() ?: return null
        return JapaneseDate.of(year, 12, 28).format(DateTimeFormatter.ofPattern("Gy"))
    }

}