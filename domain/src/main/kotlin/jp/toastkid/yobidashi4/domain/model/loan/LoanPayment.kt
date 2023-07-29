package jp.toastkid.yobidashi4.domain.model.loan

data class LoanPayment(
    val monthlyPayment: Long,
    val paymentSchedule: List<PaymentDetail>
)