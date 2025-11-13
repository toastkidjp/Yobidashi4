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

class UnixTimeConverterServiceTest {

    @InjectMockKs
    private lateinit var unixTimeConverterService: UnixTimeConverterService

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
        assertTrue(unixTimeConverterService.title().isNotBlank())
    }

    @Test
    fun defaultFirstInputValue() {
        assertTrue(unixTimeConverterService.defaultFirstInputValue().isNotBlank())
    }

    @Test
    fun defaultSecondInputValue() {
        assertTrue(unixTimeConverterService.defaultSecondInputValue().isNotBlank())
    }

    @ParameterizedTest
    @CsvSource(
        "''",
        "' '",
        "test1"
    )
    fun firstInputActionReturnsNullCases(input: String) {
        assertNull(unixTimeConverterService.firstInputAction(input))
    }

    @ParameterizedTest
    @CsvSource(
        "-1, 1970-01-01 08:59:59",
        "0, 1970-01-01 09:00:00",
        "1, 1970-01-01 09:00:00",
        "100, 1970-01-01 09:00:00",
        "2000, 1970-01-01 09:00:02",
        "160012233432, 1975-01-27 08:50:33",
        "1600122334320, 2020-09-15 07:25:34",
        "16001223343200, 2477-01-21 17:15:43",
        "160012233432000, 7040-08-02 19:37:12",
    )
    fun firstInputAction(input: String, expected: String) {
        assertEquals(expected, unixTimeConverterService.firstInputAction(input))
    }

    @ParameterizedTest
    @CsvSource(
        "'', null",
        "' ', null",
        "test2, null",
        "2023-02-13, null",
        "-9-02-13 11:22:33, null",
        "9-02-13 11:22:33, null",
        "99-02-13 11:22:33, null",
        "999-02-13 11:22:33, null",
        "-009-02-13 11:22:33, null",
        "0009-02-13 11:22:33, -61879412247000",
        "0099-02-13 11:22:33, -59039271447000",
        "0999-02-13 11:22:33, -30638036247000",
        "1963-02-13 11:22:33, -217201047000",
        "2023-02-13 11:22:33, 1676254953000",
        "2045-02-13 11:22:33, 2370565353000",
        nullValues = ["null"]
    )
    fun secondInputAction(input: String, expected: String?) {
        assertEquals(expected, unixTimeConverterService.secondInputAction(input))
    }

    @Test
    fun checkValues() {
        assertTrue(unixTimeConverterService.title().isNotBlank())
        assertTrue(unixTimeConverterService.firstInputLabel().isNotBlank())
        assertTrue(unixTimeConverterService.secondInputLabel().isNotBlank())
        assertTrue(unixTimeConverterService.defaultFirstInputValue().isNotBlank())
        assertTrue(unixTimeConverterService.defaultSecondInputValue().isNotBlank())
    }
    
}