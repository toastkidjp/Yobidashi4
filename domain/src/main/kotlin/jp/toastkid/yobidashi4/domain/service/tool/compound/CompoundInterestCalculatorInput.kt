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
            val capital = capitalInput?.replace(REPLACE_TARGET, "")?.toDoubleOrNull() ?: return null
            val installment = installmentInput?.replace(REPLACE_TARGET, "")?.toLongOrNull() ?: return null
            val annualInterest = annualInterestInput?.replace(REPLACE_TARGET, "")?.toDoubleOrNull() ?: return null
            val year = yearInput?.replace(REPLACE_TARGET, "")?.toLongOrNull() ?: return null

            return CompoundInterestCalculatorInput(capital, installment, annualInterest, year)
        }

    }
}