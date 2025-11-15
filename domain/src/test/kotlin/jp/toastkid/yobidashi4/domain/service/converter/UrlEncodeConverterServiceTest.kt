package jp.toastkid.yobidashi4.domain.service.converter

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class UrlEncodeConverterServiceTest {

    @InjectMockKs
    private lateinit var urlEncodeConverterService: UrlEncodeConverterService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun title() {
        assertTrue(urlEncodeConverterService.title().isNotBlank())
    }

    @Test
    fun defaultFirstInputValue() {
        assertTrue(urlEncodeConverterService.defaultFirstInputValue().isNotBlank())
    }

    @Test
    fun defaultSecondInputValue() {
        assertTrue(urlEncodeConverterService.defaultSecondInputValue().isNotBlank())
    }

    @ParameterizedTest
    @CsvSource(
        "'', ''",
        "' ', +",
        "東京特許 許可局, %E6%9D%B1%E4%BA%AC%E7%89%B9%E8%A8%B1+%E8%A8%B1%E5%8F%AF%E5%B1%80"
    )
    fun firstInputAction(input: String, expected: String) {
        assertEquals(expected, urlEncodeConverterService.firstInputAction(input))
    }

    @ParameterizedTest
    @CsvSource(
        "'', ''",
        "' ', ' '",
        "%E6%9D%B1%E4%BA%AC%E7%89%B9%E8%A8%B1+%E8%A8%B1%E5%8F%AF%E5%B1%80, 東京特許 許可局"
    )
    fun secondInputAction(input: String, expected: String) {
        assertEquals(expected, urlEncodeConverterService.secondInputAction(input))
    }

    @Test
    fun checkValues() {
        assertTrue(urlEncodeConverterService.title().isNotBlank())
        assertTrue(urlEncodeConverterService.firstInputLabel().isNotBlank())
        assertTrue(urlEncodeConverterService.secondInputLabel().isNotBlank())
        assertTrue(urlEncodeConverterService.defaultFirstInputValue().isNotBlank())
        assertTrue(urlEncodeConverterService.defaultSecondInputValue().isNotBlank())
    }

    @Test
    fun exception() {
        assertNull(urlEncodeConverterService.secondInputAction("%7s"))
    }

}