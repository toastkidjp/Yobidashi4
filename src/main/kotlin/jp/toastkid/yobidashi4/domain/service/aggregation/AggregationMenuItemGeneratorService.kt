package jp.toastkid.yobidashi4.domain.service.aggregation

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.swing.JOptionPane
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory

class AggregationMenuItemGeneratorService : KoinComponent {

    operator fun invoke(
            menuTitle: String,
            message: String,
            aggregator: (String) -> AggregationResult,
            resultConsumer: (String, AggregationResult) -> Unit = { _, _ -> }
    ) {
        val defaultInput = LocalDate.now().format(DATE_FORMATTER)
        val keyword = JOptionPane.showInputDialog(
            null,
            "$message ex)$defaultInput",
            defaultInput
        )

        if (keyword.isNullOrBlank()) {
            return
        }

        CoroutineScope(Dispatchers.Default).launch {
            try {
                val result = withContext(Dispatchers.IO) { aggregator(keyword) }

                if (result.isEmpty()) {
                    JOptionPane.showConfirmDialog(null, "Result is empty.")
                    return@launch
                }

                resultConsumer("$keyword ${result.resultTitleSuffix()}", result)
            } catch (e: Exception) {
                LoggerFactory.getLogger(javaClass).debug("Aggregation error.", e)
                JOptionPane.showConfirmDialog(null, e)
            }
        }
    }

    companion object {

        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM")

    }

}