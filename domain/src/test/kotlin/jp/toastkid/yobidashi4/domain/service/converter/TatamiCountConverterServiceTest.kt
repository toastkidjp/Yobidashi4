package jp.toastkid.yobidashi4.domain.service.converter

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TatamiCountConverterServiceTest {

    @InjectMockKs
    private lateinit var tatamiCountConverter: TatamiCountConverterService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun checkValues() {
        Assertions.assertTrue(tatamiCountConverter.title().isNotBlank())
        Assertions.assertTrue(tatamiCountConverter.firstInputLabel().isNotBlank())
        Assertions.assertTrue(tatamiCountConverter.secondInputLabel().isNotBlank())
        Assertions.assertTrue(tatamiCountConverter.defaultFirstInputValue().isNotBlank())
        Assertions.assertTrue(tatamiCountConverter.defaultSecondInputValue().isNotBlank())
    }

    @Test
    fun test() {
        assertEquals("12.96", tatamiCountConverter.firstInputAction("8"))
        assertEquals("18.52", tatamiCountConverter.secondInputAction("30"))
    }

    @Test
    fun incorrectInput() {
        assertNull(tatamiCountConverter.firstInputAction("あ"))
        assertNull(tatamiCountConverter.secondInputAction("あ"))
    }

}