package jp.toastkid.yobidashi4.presentation.compound

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.service.tool.compound.CompoundInterestCalculatorInput
import jp.toastkid.yobidashi4.domain.service.tool.compound.CompoundInterestCalculatorService
import jp.toastkid.yobidashi4.presentation.compound.viewmodel.CompoundInterestCalculatorViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CompoundInterestCalculatorViewKtTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(CompoundInterestCalculatorViewModel::class)
        every { anyConstructed<CompoundInterestCalculatorViewModel>().installmentInput() } returns TextFieldState()
        every { anyConstructed<CompoundInterestCalculatorViewModel>().annualInterestInput() } returns TextFieldState()
        every { anyConstructed<CompoundInterestCalculatorViewModel>().capitalInput() } returns TextFieldState()
        every { anyConstructed<CompoundInterestCalculatorViewModel>().yearInput() } returns TextFieldState()
        every { anyConstructed<CompoundInterestCalculatorViewModel>().result() } returns
                CompoundInterestCalculatorService().invoke(
                    CompoundInterestCalculatorInput(
                        1.0, 120000, 0.01, 10
                    )
                ).itemArrays().toList()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun compoundInterestCalculatorView() {
        runDesktopComposeUiTest {
            setContent {
                CompoundInterestCalculatorView()
            }
        }
    }
}