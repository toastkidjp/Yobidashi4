package jp.toastkid.yobidashi4.domain.service.table

import java.nio.file.Files
import java.nio.file.Paths
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult

class TableContentExporter {

    operator fun invoke(aggregationResult: AggregationResult) {
        val outputFolder = Paths.get(EXPORT_TO)
        if (Files.exists(outputFolder).not()) {
            Files.createDirectories(outputFolder)
        }
        Files.write(
            outputFolder.resolve("${aggregationResult.resultTitleSuffix().replace(":", "_")}.tsv"),
            aggregationResult.itemArrays().map { it.joinToString(COLUMN_DELIMITER) }
        )
    }

    companion object {

        private const val COLUMN_DELIMITER = "\t"

        private const val EXPORT_TO = "user/table"

        fun exportTo() = EXPORT_TO

    }

}