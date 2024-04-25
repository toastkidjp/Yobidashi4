package jp.toastkid.yobidashi4.domain.service.tool.compound

data class CompoundInterestCalculatorInput(
    val capital: Double,
    val installment: Long,
    val annualInterest: Double,
    val year: Long
) {

    companion object {

        private const val REPLACE_TARGET = ","

        fun from(
            capitalInput: String?,
            installmentInput: String?,
            annualInterestInput: String?,
            yearInput: String?,
        ): CompoundInterestCalculatorInput? {
            if (capitalInput.isNullOrBlank() || installmentInput.isNullOrBlank() || annualInterestInput.isNullOrBlank() || yearInput.isNullOrBlank()) {
                return null
            }

            val capital = capitalInput.toDouble()
            val installment = installmentInput.toLong()
            val annualInterest = annualInterestInput.toDouble()
            val year = yearInput.toLong()

            return CompoundInterestCalculatorInput(capital, installment, annualInterest, year)
        }

    }
}