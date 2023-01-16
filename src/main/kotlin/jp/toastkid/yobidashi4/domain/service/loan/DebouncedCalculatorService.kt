package jp.toastkid.yobidashi4.domain.service.loan

import jp.toastkid.yobidashi4.domain.model.loan.Factor
import jp.toastkid.yobidashi4.domain.model.loan.LoanPayment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DebouncedCalculatorService(
    private val inputChannel: Channel<String>,
    private val currentFactorProvider: () -> Factor,
    private val onResult: (LoanPayment) -> Unit,
    private val calculator: LoanCalculator = LoanCalculator(),
    private val debounceMillis: Long = 1000,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) {

    @FlowPreview
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