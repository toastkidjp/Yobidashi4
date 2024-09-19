package jp.toastkid.yobidashi4.domain.model.loan

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LoanPaymentTest {

    @Test
    fun totalInterestAmount() {
        assertEquals(
            16000,
            LoanPayment(
                20000,
                listOf(
                    PaymentDetail(12000.0, 8000.0, 20000),
                    PaymentDetail(12000.0, 8000.0, 20000)
                )
            ).totalInterestAmount()
        )
    }

}
