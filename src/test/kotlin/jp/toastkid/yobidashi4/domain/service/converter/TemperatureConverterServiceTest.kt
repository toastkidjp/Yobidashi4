package jp.toastkid.yobidashi4.domain.service.converter

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import kotlin.test.assertEquals
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
    fun test() {
        assertEquals("25.0", temperatureConverterService.firstInputAction("77.0"))
        assertEquals("77.0", temperatureConverterService.secondInputAction("25"))
    }

}