package jp.toastkid.yobidashi4.domain.service.converter

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import kotlin.test.assertEquals
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
    fun test() {
        assertEquals("1600.00", distanceConverterService.firstInputAction("20"))
        assertEquals("40.00", distanceConverterService.secondInputAction("3200"))
    }

}