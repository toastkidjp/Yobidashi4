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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DebouncedCalculatorService(
    private val inputChannel: Channel<String>,
    private val currentFactorProvider: () -> Factor,
    private val onResult: (LoanPayment) -> Unit,
    private val calculator: LoanCalculator = LoanCalculator(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    operator fun invoke() {
        CoroutineScope(ioDispatcher).launch {
            inputChannel
                .receiveAsFlow()
                .distinctUntilChanged()
                .collect {
                    val factor = currentFactorProvider()
                    val payment = calculator(factor)

                    onResult(payment)
                }
        }
    }

}