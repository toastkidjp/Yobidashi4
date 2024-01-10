package jp.toastkid.yobidashi4.presentation.compound

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
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
        every { anyConstructed<CompoundInterestCalculatorViewModel>().installmentInput() } returns TextFieldValue()
        every { anyConstructed<CompoundInterestCalculatorViewModel>().annualInterestInput() } returns TextFieldValue()
        every { anyConstructed<CompoundInterestCalculatorViewModel>().capitalInput() } returns TextFieldValue()
        every { anyConstructed<CompoundInterestCalculatorViewModel>().yearInput() } returns TextFieldValue()
        every { anyConstructed<CompoundInterestCalculatorViewModel>().result() } returns
                CompoundInterestCalculatorService().invoke(
                    CompoundInterestCalculatorInput(
                        1.0, 120000, 0.01, 10
                    )
                )
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
                verify { anyConstructed<CompoundInterestCalculatorViewModel>().result() }
            }
        }
    }
}