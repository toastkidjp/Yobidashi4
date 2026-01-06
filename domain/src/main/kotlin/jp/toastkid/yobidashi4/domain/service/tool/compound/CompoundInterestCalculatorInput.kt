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

            val capital = capitalInput.toDoubleOrNull() ?: return null
            val installment = installmentInput.toLongOrNull() ?: return null
            val annualInterest = annualInterestInput.toDoubleOrNull() ?: return null
            val year = yearInput.toLongOrNull() ?: return null

            return CompoundInterestCalculatorInput(capital, installment, annualInterest, year)
        }

    }
}