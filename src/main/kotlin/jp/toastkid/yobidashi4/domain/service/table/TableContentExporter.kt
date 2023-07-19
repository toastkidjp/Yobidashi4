package jp.toastkid.yobidashi4.domain.service.table

import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult

class TableContentExporter {

    operator fun invoke(aggregationResult: AggregationResult) {
        val outputFolder = Path.of(EXPORT_TO)
        if (Files.exists(outputFolder).not()) {
            Files.createDirectories(outputFolder)
        }
        Files.write(
            outputFolder.resolve("${aggregationResult.resultTitleSuffix().replace(":", ACCEPTABLE_CHARACTER)}.tsv"),
            aggregationResult.itemArrays().map { it.joinToString(COLUMN_DELIMITER) }
        )
    }

    companion object {

        private const val COLUMN_DELIMITER = "\t"

        private const val ACCEPTABLE_CHARACTER = "_"

        private const val EXPORT_TO = "user/table"

        fun exportTo() = EXPORT_TO

    }

}