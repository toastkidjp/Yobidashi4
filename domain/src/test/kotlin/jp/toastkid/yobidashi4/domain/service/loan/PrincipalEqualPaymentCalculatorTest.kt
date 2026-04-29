/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.loan

import jp.toastkid.yobidashi4.domain.model.loan.Factor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PrincipalEqualPaymentCalculatorTest {

    private val calculator = PrincipalEqualPaymentCalculator()

    @Test
    @DisplayName("1200万円、10年払い、金利1.2%の計算結果が正しいか検証")
    fun testCalculate() {
        val factor = Factor(
            amount = 12_000_000L,
            term = 10,
            interestRate = 1.2,
            downPayment = 0,
            managementFee = 10_000,
            renovationReserves = 5_000
        )

        val result = calculator.invoke(factor)

        val totalMonths = 120
        assertEquals(totalMonths, result.paymentSchedule.size, "返済回数が120回であること")

        val expectedPrincipal = 100_000.0
        result.paymentSchedule.forEach { detail ->
            assertEquals(expectedPrincipal, detail.principal, 0.001, "毎月の元金返済額が10万円であること")
        }

        val firstInterest = result.paymentSchedule.first().interest
        assertEquals(12_000.0, firstInterest, 0.001, "初回の利息が1.2万円であること")

        assertEquals(127_000L, result.paymentSchedule.first().amount, "初回の支払総額が正しいこと")

        val firstMonthAmount = result.paymentSchedule[0].amount
        val secondMonthAmount = result.paymentSchedule[1].amount
        assertTrue(firstMonthAmount > secondMonthAmount, "2回目は初回より支払額が少なくなること")

        val lastInterest = result.paymentSchedule.last().interest
        assertEquals(100.0, lastInterest, 0.001, "最終回の利息が100円であること")
    }

    @Test
    @DisplayName("頭金がある場合に借入元本が減額されて計算されるか検証")
    fun testDownPayment() {
        val factor = Factor(
            amount = 10_000_000L,
            term = 10,
            interestRate = 1.0,
            downPayment = 2_000_000,
            managementFee = 0,
            renovationReserves = 0
        )

        val result = calculator.invoke(factor)

        val expectedPrincipal = 8_000_000.0 / 120
        assertEquals(expectedPrincipal, result.paymentSchedule.first().principal, 0.001)
    }

}