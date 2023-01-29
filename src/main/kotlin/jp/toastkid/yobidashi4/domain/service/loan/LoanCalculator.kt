package jp.toastkid.yobidashi4.domain.service.loan

import jp.toastkid.yobidashi4.domain.model.loan.Factor
import jp.toastkid.yobidashi4.domain.model.loan.LoanPayment
import jp.toastkid.yobidashi4.domain.model.loan.PaymentDetail
import kotlin.math.max
import kotlin.math.pow

class LoanCalculator {

    operator fun invoke(factor: Factor) : LoanPayment {
        val paymentCount = (factor.term * 12).toDouble()
        val convertedRate = factor.interestRate / 100.0
        val actualTotalAmount = factor.amount - factor.downPayment
        var currentAmount = actualTotalAmount

        val poweredMonthlyInterestRate = (1 + convertedRate / 12).pow(paymentCount)

        val numerator = (actualTotalAmount * convertedRate) / 12 * poweredMonthlyInterestRate
        val denominator = poweredMonthlyInterestRate - 1
        val monthlyPaymentToBank = max(numerator / denominator, 0.0)

        val monthlyPayment = monthlyPaymentToBank.toLong() + factor.managementFee + factor.renovationReserves
        return LoanPayment(monthlyPayment, (0 until paymentCount.toInt()).map {
            val monthlyInterest = currentAmount * (convertedRate / 12)
            val monthlyActualReturning = monthlyPaymentToBank - monthlyInterest
            currentAmount -= (monthlyPaymentToBank - monthlyInterest).toLong()
            // println("monthlyPayment $monthlyPaymentToBank monthlyActualReturning $monthlyActualReturning monthlyInterest $monthlyInterest currentAmount $currentAmount")
            PaymentDetail(monthlyActualReturning, monthlyInterest, currentAmount)
        })
    }

}


fun main() {
    LoanCalculator().invoke(Factor(
        30_000_000,
        35,
        1.0,
        0,
        0,
        0
    ))
}