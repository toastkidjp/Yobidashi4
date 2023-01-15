package jp.toastkid.yobidashi4.domain.service.aggregation

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.text.NumberFormat
import javax.swing.text.NumberFormatter

internal class IntFormatterFactoryServiceTest {

    private lateinit var intFormatterFactoryService: IntFormatterFactoryService

    @BeforeEach
    fun setUp() {
        intFormatterFactoryService = IntFormatterFactoryService()

        mockkStatic(NumberFormat::class)
        every { NumberFormat.getInstance() }.answers { mockk() }

        mockkConstructor(NumberFormatter::class)
        every { anyConstructed<NumberFormatter>().setValueClass(any()) }.answers { Unit }
        every { anyConstructed<NumberFormatter>().setMinimum(any()) }.answers { Unit }
        every { anyConstructed<NumberFormatter>().setAllowsInvalid(any()) }.answers { Unit }
    }

    @Test
    fun test() {
        intFormatterFactoryService()

        verify (exactly = 1) { NumberFormat.getInstance() }
        verify (exactly = 1) { anyConstructed<NumberFormatter>().setValueClass(Integer::class.java) }
        verify (exactly = 1) { anyConstructed<NumberFormatter>().setMinimum(0) }
        verify (exactly = 1) { anyConstructed<NumberFormatter>().setAllowsInvalid(false) }
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

}