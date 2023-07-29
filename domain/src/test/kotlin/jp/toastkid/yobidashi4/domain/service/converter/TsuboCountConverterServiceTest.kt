package jp.toastkid.yobidashi4.domain.service.converter

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TsuboCountConverterServiceTest {

    @InjectMockKs
    private lateinit var converter: TsuboCountConverterService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test() {
        Assertions.assertEquals("324.00", converter.firstInputAction("100"))
        Assertions.assertEquals("50.00", converter.secondInputAction("162.00"))
    }

}