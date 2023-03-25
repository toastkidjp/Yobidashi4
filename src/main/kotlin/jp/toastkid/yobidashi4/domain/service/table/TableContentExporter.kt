package jp.toastkid.yobidashi4.domain.service.table

import java.nio.file.Files
import java.nio.file.Paths
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult

class TableContentExporter {

    operator fun invoke(aggregationResult: AggregationResult) {
        val outputFolder = Paths.get("user/table")
        if (Files.exists(outputFolder).not()) {
            Files.createDirectories(outputFolder)
        }
        Files.write(
            outputFolder.resolve("${aggregationResult.resultTitleSuffix().replace(":", "_")}.tsv"),
            aggregationResult.itemArrays().map { it.joinToString("\t") }.joinToString("\n") { it }.toByteArray()
        )
    }

}