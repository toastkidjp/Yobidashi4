/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.table

import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import java.nio.file.Files
import java.nio.file.Path

class TableContentExporter {

    operator fun invoke(aggregationResult: AggregationResult) {
        val outputFolder = Path.of(EXPORT_TO)
        if (Files.exists(outputFolder).not()) {
            Files.createDirectories(outputFolder)
        }
        Files.write(
            outputFolder.resolve("${aggregationResult.title().replace(":", ACCEPTABLE_CHARACTER)}.tsv"),
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