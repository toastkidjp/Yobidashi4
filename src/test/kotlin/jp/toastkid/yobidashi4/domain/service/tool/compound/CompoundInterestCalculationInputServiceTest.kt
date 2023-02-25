package jp.toastkid.yobidashi4.domain.service.tool.compound

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import javax.swing.JComponent
import javax.swing.JFormattedTextField
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.text.NumberFormatter
import kotlin.test.Ignore
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach

internal class CompoundInterestCalculationInputServiceTest {

    private lateinit var compoundInterestCalculationInputService: CompoundInterestCalculationInputService

    @MockK
    private lateinit var intFormatter: NumberFormatter

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { intFormatter.install(any()) }.answers { Unit }

        compoundInterestCalculationInputService = CompoundInterestCalculationInputService(intFormatter)

        mockkStatic(JOptionPane::class)

        mockkConstructor(JFormattedTextField::class)
        mockkConstructor(JPanel::class)
        every { anyConstructed<JPanel>().setLayout(any()) }.answers { Unit }
        every { anyConstructed<JPanel>().add(any<JComponent>()) }.answers { mockk() }
    }

    @Ignore
    fun test() {
        every { JOptionPane.showConfirmDialog(null, any<JComponent>()) }.answers { JOptionPane.OK_OPTION }
        every { anyConstructed<JFormattedTextField>().getText() }.answers { "1" }

        compoundInterestCalculationInputService.invoke()

        verify(atLeast = 1) { intFormatter.install(any()) }
        verify(exactly = 1) { anyConstructed<JPanel>().setLayout(any()) }
        verify(atLeast = 1) { anyConstructed<JPanel>().add(any<JComponent>()) }
        verify(exactly = 1) { JOptionPane.showConfirmDialog(null, any<JComponent>()) }
        verify(atLeast = 1) { anyConstructed<JFormattedTextField>().getText() }
    }

    @Ignore
    fun testCancelCase() {
        every { JOptionPane.showConfirmDialog(null, any<JComponent>()) }.answers { JOptionPane.CANCEL_OPTION }
        every { anyConstructed<JFormattedTextField>().getText() }.answers { "1" }

        assertNull(compoundInterestCalculationInputService.invoke())

        verify(atLeast = 1) { intFormatter.install(any()) }
        verify(exactly = 1) { anyConstructed<JPanel>().setLayout(any()) }
        verify(atLeast = 1) { anyConstructed<JPanel>().add(any<JComponent>()) }
        verify(exactly = 1) { JOptionPane.showConfirmDialog(null, any<JComponent>()) }
        verify(exactly = 0) { anyConstructed<JFormattedTextField>().getText() }
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

}