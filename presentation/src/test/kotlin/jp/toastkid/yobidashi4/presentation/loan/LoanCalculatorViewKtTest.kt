package jp.toastkid.yobidashi4.presentation.loan

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.loan.PaymentDetail
import jp.toastkid.yobidashi4.presentation.loan.viewmodel.LoanCalculatorViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoanCalculatorViewKtTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(LoanCalculatorViewModel::class)
        every { anyConstructed<LoanCalculatorViewModel>().scheduleState() } returns listOf(
            PaymentDetail(1.0, 0.99, 2000),
            PaymentDetail(1.2, 1.0, 2000),
        )
        every { anyConstructed<LoanCalculatorViewModel>().loanAmount() } returns "0"
        every { anyConstructed<LoanCalculatorViewModel>().loanTerm() } returns "0"
        every { anyConstructed<LoanCalculatorViewModel>().interestRate() } returns "0"
        every { anyConstructed<LoanCalculatorViewModel>().downPayment() } returns "0"
        every { anyConstructed<LoanCalculatorViewModel>().managementFee() } returns "0"
        every { anyConstructed<LoanCalculatorViewModel>().renovationReserves() } returns "0"
        every { anyConstructed<LoanCalculatorViewModel>().roundToIntSafely(any()) } returns "0"
        every { anyConstructed<LoanCalculatorViewModel>().launch() } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun test() {
        runDesktopComposeUiTest {
            setContent {
                LoanCalculatorView()
            }
        }
    }
}