/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.loan

import jp.toastkid.yobidashi4.domain.model.loan.Factor
import jp.toastkid.yobidashi4.domain.model.loan.LoanPayment
import jp.toastkid.yobidashi4.domain.model.loan.PaymentDetail
import kotlin.math.roundToLong

class PrincipalEqualPaymentCalculator {

    operator fun invoke(factor: Factor): LoanPayment {
        val totalMonths = factor.term * 12
        val loanAmount = factor.amount - factor.downPayment

        val monthlyPrincipal = loanAmount.toDouble() / totalMonths
        val monthlyInterestRate = factor.interestRate / 100.0 / 12.0

        val schedule = mutableListOf<PaymentDetail>()
        var remainingbalance = loanAmount.toDouble()

        for (i in 1..totalMonths) {
            val interest = remainingbalance * monthlyInterestRate

            val totalAmount = (monthlyPrincipal + interest + factor.managementFee + factor.renovationReserves).roundToLong()

            schedule.add(
                PaymentDetail(
                    principal = monthlyPrincipal,
                    interest = interest,
                    amount = totalAmount
                )
            )

            remainingbalance -= monthlyPrincipal
        }

        return LoanPayment(
            monthlyPayment = schedule.firstOrNull()?.amount ?: -1L,
            paymentSchedule = schedule
        )
    }

}
