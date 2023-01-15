package jp.toastkid.yobidashi4.domain.service.tool.compound

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import javax.swing.JMenuItem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CompoundInterestCalculatorMenuGeneratorServiceTest {

    private lateinit var compoundInterestCalculatorMenuGeneratorService: CompoundInterestCalculatorMenuGeneratorService

    @MockK
    private lateinit var inputService: CompoundInterestCalculationInputService

    @MockK
    private lateinit var calculatorService: CompoundInterestCalculatorService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        compoundInterestCalculatorMenuGeneratorService = CompoundInterestCalculatorMenuGeneratorService(
                inputService, calculatorService
        )

        mockkConstructor(JMenuItem::class)
        every { anyConstructed<JMenuItem>().addActionListener(any()) }.answers { Unit }
    }

    @Test
    fun test() {
        compoundInterestCalculatorMenuGeneratorService.invoke()

        verify(exactly = 1) { anyConstructed<JMenuItem>().addActionListener(any()) }
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

}