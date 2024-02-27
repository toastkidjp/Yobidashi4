package jp.toastkid.yobidashi4.domain.service.converter

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TemperatureConverterServiceTest {

    @InjectMockKs
    private lateinit var temperatureConverterService: TemperatureConverterService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun checkValues() {
        Assertions.assertTrue(temperatureConverterService.title().isNotBlank())
        Assertions.assertTrue(temperatureConverterService.firstInputLabel().isNotBlank())
        Assertions.assertTrue(temperatureConverterService.secondInputLabel().isNotBlank())
        Assertions.assertTrue(temperatureConverterService.defaultFirstInputValue().isNotBlank())
        Assertions.assertTrue(temperatureConverterService.defaultSecondInputValue().isNotBlank())
    }

    @Test
    fun test() {
        assertEquals("25.0", temperatureConverterService.firstInputAction("77.0"))
        assertEquals("77.0", temperatureConverterService.secondInputAction("25"))
    }

    @Test
    fun incorrectInput() {
        assertNull(temperatureConverterService.firstInputAction("あ"))
        assertNull(temperatureConverterService.secondInputAction("あ"))
    }

}