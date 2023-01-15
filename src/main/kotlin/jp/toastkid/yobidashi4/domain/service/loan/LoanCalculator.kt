package jp.toastkid.yobidashi4.domain.service.loan

import jp.toastkid.yobidashi4.domain.model.loan.Factor
import kotlin.math.max
import kotlin.math.pow

class LoanCalculator {

    operator fun invoke(factor: Factor) : Long {
        val paymentCount = (factor.term * 12).toDouble()
        val convertedRate = factor.interestRate / 100.0
        val poweredMonthlyInterestRate = (1 + convertedRate / 12).pow(paymentCount)

        val numerator = ((factor.amount - factor.downPayment) * convertedRate) / 12 * poweredMonthlyInterestRate
        val denominator = poweredMonthlyInterestRate - 1

        return (max(numerator / denominator, 0.0)).toLong() + factor.managementFee + factor.renovationReserves
    }

}