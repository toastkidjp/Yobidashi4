package jp.toastkid.yobidashi4.domain.service.converter

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JapaneseEraConverterServiceTest {

    private lateinit var japaneseEraConverterService: JapaneseEraConverterService

    @BeforeEach
    fun setUp() {
        japaneseEraConverterService = JapaneseEraConverterService()
    }

    @AfterEach
    fun tearDown() {
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

    @Test
    fun firstInputAction() {
        assertNull(japaneseEraConverterService.firstInputAction("明治5"))
        assertEquals("1874", japaneseEraConverterService.firstInputAction("明治6"))
        assertEquals("1900", japaneseEraConverterService.firstInputAction("明治33"))
        assertEquals("1914", japaneseEraConverterService.firstInputAction("大正2"))
        assertEquals("1988", japaneseEraConverterService.firstInputAction("昭和63"))
        assertEquals("2018", japaneseEraConverterService.firstInputAction("平成30"))
        assertEquals("2019", japaneseEraConverterService.firstInputAction("令和1"))
    }

    @Test
    fun firstInputActionIrregularInput() {
        assertNull(japaneseEraConverterService.firstInputAction("63"))
        assertNull(japaneseEraConverterService.firstInputAction("63昭和"))
        assertNull(japaneseEraConverterService.firstInputAction("昭和初期"))
        assertNull(japaneseEraConverterService.firstInputAction("1984"))
    }

    @Test
    fun secondInputAction() {
        assertNull(japaneseEraConverterService.secondInputAction("昭和"))
        assertEquals("明治44", japaneseEraConverterService.secondInputAction("1911"))
        assertEquals("大正1", japaneseEraConverterService.secondInputAction("1912"))
        assertEquals("大正14", japaneseEraConverterService.secondInputAction("1925"))
        assertEquals("昭和1", japaneseEraConverterService.secondInputAction("1926"))
        assertEquals("昭和2", japaneseEraConverterService.secondInputAction("1927"))
        assertEquals("平成2", japaneseEraConverterService.secondInputAction("1990"))
        assertEquals("令和1", japaneseEraConverterService.secondInputAction("2019"))
        assertEquals("令和2", japaneseEraConverterService.secondInputAction("2020"))
    }
}