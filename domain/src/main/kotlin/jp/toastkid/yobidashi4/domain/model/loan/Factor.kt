package jp.toastkid.yobidashi4.domain.model.loan

class Factor(
    val amount: Long,
    val term: Int,
    val interestRate: Double,
    val downPayment: Int,
    val managementFee: Int,
    val renovationReserves: Int
)