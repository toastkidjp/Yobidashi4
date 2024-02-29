package jp.toastkid.yobidashi4.domain.service.converter

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DistanceConverterServiceTest {

    @InjectMockKs
    private lateinit var distanceConverterService: DistanceConverterService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun checkValues() {
        assertTrue(distanceConverterService.title().isNotBlank())
        assertTrue(distanceConverterService.firstInputLabel().isNotBlank())
        assertTrue(distanceConverterService.secondInputLabel().isNotBlank())
        assertTrue(distanceConverterService.defaultFirstInputValue().isNotBlank())
        assertTrue(distanceConverterService.defaultSecondInputValue().isNotBlank())
    }

    @Test
    fun test() {
        assertEquals("1600.00", distanceConverterService.firstInputAction("20"))
        assertEquals("40.00", distanceConverterService.secondInputAction("3200"))
    }

    @Test
    fun incorrectInput() {
        assertNull(distanceConverterService.firstInputAction("あ"))
        assertNull(distanceConverterService.secondInputAction("あ"))
    }

}