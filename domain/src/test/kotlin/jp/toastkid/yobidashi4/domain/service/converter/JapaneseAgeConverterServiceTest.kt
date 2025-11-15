package jp.toastkid.yobidashi4.domain.service.converter

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
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
        "大正13, 99"
    )
    fun firstInputAction(input: String, expected: String) {
        assertEquals(expected, japaneseAgeConverterService.firstInputAction(input))
    }

    @ParameterizedTest
    @CsvSource(
        "明治5",
        "大正",
        "1984",
        "大正浪漫",
        "大正16"
    )
    fun firstInputActionIrregularCase(input: String) {
        assertNull(japaneseAgeConverterService.firstInputAction(input))
    }

    @Test
    fun secondInputAction() {
        assertEquals("大正14", japaneseAgeConverterService.secondInputAction("98"))
        assertEquals("大正15", japaneseAgeConverterService.secondInputAction("97"))
        assertEquals("昭和2", japaneseAgeConverterService.secondInputAction("96"))
        assertEquals("昭和62", japaneseAgeConverterService.secondInputAction("36"))
        assertEquals("昭和63", japaneseAgeConverterService.secondInputAction("35"))
        assertEquals("平成1", japaneseAgeConverterService.secondInputAction("34"))
        assertEquals("令和1", japaneseAgeConverterService.secondInputAction("4"))
        assertEquals("令和2",  japaneseAgeConverterService.secondInputAction("3"))
    }

    @Test
    fun secondInputActionIrregularCase() {
        assertNull(japaneseAgeConverterService.secondInputAction("2019"))
        assertNull(japaneseAgeConverterService.secondInputAction("大正"))
    }

}