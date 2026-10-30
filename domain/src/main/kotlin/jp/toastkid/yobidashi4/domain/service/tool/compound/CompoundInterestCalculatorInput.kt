/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.tool.compound

data class CompoundInterestCalculatorInput(
    val capital: Double,
    val installment: Long,
    val annualInterest: Double,
    val year: Long
) {

    companion object {

        fun from(
            capitalInput: String?,
            installmentInput: String?,
            annualInterestInput: String?,
            yearInput: String?,
        ): CompoundInterestCalculatorInput? {
            val capital = capitalInput?.toDoubleOrNull() ?: return null
            val installment = installmentInput?.toLongOrNull() ?: return null
            val annualInterest = annualInterestInput?.toDoubleOrNull() ?: return null
            val year = yearInput?.toLongOrNull() ?: return null

            return CompoundInterestCalculatorInput(capital, installment, annualInterest, year)
        }

    }
}