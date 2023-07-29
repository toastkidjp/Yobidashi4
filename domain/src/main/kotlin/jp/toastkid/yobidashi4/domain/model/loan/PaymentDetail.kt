package jp.toastkid.yobidashi4.domain.model.loan

data class PaymentDetail(
    val principal: Double,
    val interest: Double,
    val amount: Long
) {
}