package jp.toastkid.yobidashi4.domain.service.tool.compound

import javax.swing.JOptionPane
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory

class CompoundInterestCalculatorMenuGeneratorService(
    private val inputService: CompoundInterestCalculationInputService = CompoundInterestCalculationInputService(),
    private val calculatorService: CompoundInterestCalculatorService = CompoundInterestCalculatorService(),
    private val resultConsumer: (String, AggregationResult) -> Unit = { _, _ -> }
) : KoinComponent {

    operator fun invoke() {
        val (installment, annualInterest, year) = inputService.invoke() ?: return

        CoroutineScope(Dispatchers.Default).launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    calculatorService(installment, (annualInterest * 0.01), year)
                }

                if (result.isEmpty()) {
                    JOptionPane.showConfirmDialog(null, "Result is empty.")
                    return@launch
                }

                // "Compound interest calculation. Installment = $installment, Annual interest = $annualInterest, Year = $year"
                resultConsumer("$installment, $annualInterest, $year", result)
            } catch (e: Exception) {
                LoggerFactory.getLogger(javaClass).warn("Calculation error.", e)
                JOptionPane.showConfirmDialog(null, e)
            }
        }
    }

}