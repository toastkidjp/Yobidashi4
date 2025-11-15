package jp.toastkid.yobidashi4.domain.service.converter

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.LocalDate
import java.time.chrono.JapaneseDate

class JapaneseAgeConverterServiceTest {

    private lateinit var japaneseAgeConverterService: JapaneseAgeConverterService

    @BeforeEach
    fun setUp() {
        japaneseAgeConverterService = JapaneseAgeConverterService()

        mockkStatic(JapaneseDate::class)
        every { JapaneseDate.now() } returns JapaneseDate.of(2023, 8, 27)
        mockkStatic(LocalDate::class)
        every { LocalDate.now() } returns LocalDate.of(2023, 8, 27)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun title() {
        assertNotNull(japaneseAgeConverterService.title())
    }

    @Test
    fun firstInputLabel() {
        assertNotNull(japaneseAgeConverterService.firstInputLabel())
    }

    @Test
    fun secondInputLabel() {
        assertNotNull(japaneseAgeConverterService.secondInputLabel())
    }

    @Test
    fun defaultFirstInputValue() {
        assertNotNull(japaneseAgeConverterService.defaultFirstInputValue())
    }

    @Test
    fun defaultSecondInputValue() {
        assertNotNull(japaneseAgeConverterService.defaultSecondInputValue())
    }

    @ParameterizedTest
    @CsvSource(
        "昭和63, 35",
        "平成2, 33",
        "令和1, 4",
        "明治43, 113",
        "大正13, 99",
        "明治5, null",
        "大正, null",
        "1984, null",
        "大正浪漫, null",
        "大正16, null",
        nullValues = ["null"]
    )
    fun firstInputAction(input: String, expected: String?) {
        assertEquals(expected, japaneseAgeConverterService.firstInputAction(input))
    }

    @ParameterizedTest
    @CsvSource(
        "98, 大正14",
        "97, 大正15",
        "96, 昭和2",
        "36, 昭和62",
        "35, 昭和63",
        "34, 平成1",
        "4, 令和1",
        "3, 令和2",
        "2019, null",
        "大正, null",
        nullValues = ["null"]
    )
    fun secondInputAction(input: String, expected: String?) {
        assertEquals(expected, japaneseAgeConverterService.secondInputAction(input))
    }

}