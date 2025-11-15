package jp.toastkid.yobidashi4.domain.service.converter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class JapaneseEraConverterServiceTest {

    private lateinit var japaneseEraConverterService: JapaneseEraConverterService

    @BeforeEach
    fun setUp() {
        japaneseEraConverterService = JapaneseEraConverterService()
    }

    @Test
    fun title() {
        assertNotNull(japaneseEraConverterService.title())
    }

    @Test
    fun firstInputLabel() {
        assertNotNull(japaneseEraConverterService.firstInputLabel())
    }

    @Test
    fun secondInputLabel() {
        assertNotNull(japaneseEraConverterService.secondInputLabel())
    }

    @Test
    fun defaultFirstInputValue() {
        assertNotNull(japaneseEraConverterService.defaultFirstInputValue())
    }

    @Test
    fun defaultSecondInputValue() {
        assertNotNull(japaneseEraConverterService.defaultSecondInputValue())
    }

    @ParameterizedTest
    @CsvSource(
        "明治6, 1874",
        "明治33, 1900",
        "大正2, 1914",
        "昭和63, 1988",
        "平成30, 2018",
        "令和1, 2019"
    )
    fun firstInputAction(input: String, expected: String) {
        assertEquals(expected, japaneseEraConverterService.firstInputAction(input))
    }

    @ParameterizedTest
    @CsvSource(
        "明治5",
        "63",
        "63昭和",
        "昭和初期",
        "1984",
        "平成31"
    )
    fun firstInputActionIrregularInput(input: String) {
        assertNull(japaneseEraConverterService.firstInputAction(input))
    }

    @Test
    fun secondInputAction() {
        assertEquals("明治44", japaneseEraConverterService.secondInputAction("1911"))
        assertEquals("大正1", japaneseEraConverterService.secondInputAction("1912"))
        assertEquals("大正14", japaneseEraConverterService.secondInputAction("1925"))
        assertEquals("昭和1", japaneseEraConverterService.secondInputAction("1926"))
        assertEquals("昭和2", japaneseEraConverterService.secondInputAction("1927"))
        assertEquals("平成2", japaneseEraConverterService.secondInputAction("1990"))
        assertEquals("令和1", japaneseEraConverterService.secondInputAction("2019"))
        assertEquals("令和2", japaneseEraConverterService.secondInputAction("2020"))
    }

    @Test
    fun secondInputActionIrregularCase() {
        assertNull(japaneseEraConverterService.secondInputAction("昭和"))
        assertNull(japaneseEraConverterService.secondInputAction("202"))
    }

}