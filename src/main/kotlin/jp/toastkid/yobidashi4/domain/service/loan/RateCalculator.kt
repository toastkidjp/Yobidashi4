package jp.toastkid.yobidashi4.domain.service.loan

import jp.toastkid.yobidashi4.domain.model.loan.Factor
import kotlin.math.max
import kotlin.math.pow

class RateCalculator {

    operator fun invoke(factor: Factor) : Long {
        val paymentCount = (factor.term * 12).toDouble()
        val convertedRate = factor.interestRate / 100.0
        val actualTotalAmount = factor.amount - factor.downPayment
        var currentAmount = actualTotalAmount

        val poweredMonthlyInterestRate = (1 + convertedRate / 12).pow(paymentCount)

        val numerator = (actualTotalAmount * convertedRate) / 12 * poweredMonthlyInterestRate
        val denominator = poweredMonthlyInterestRate - 1

        repeat(paymentCount.toInt()) {
            val monthlyPayment = max(numerator / denominator, 0.0)
            val monthlyInterest = currentAmount * (convertedRate / 12)
            val monthlyActualReturning = monthlyPayment - monthlyInterest
            currentAmount -= (monthlyPayment - monthlyInterest).toLong()
            println("monthlyPayment $monthlyPayment monthlyActualReturning $monthlyActualReturning monthlyInterest $monthlyInterest currentAmount $currentAmount")
        }
        return 0
    }

}

fun main() {
    RateCalculator().invoke(Factor(
        30_000_000,
        35,
        1.0,
        0,
        0,
        0
    ))
}