package jp.toastkid.yobidashi4.domain.service.aggregation

import java.nio.file.Files
import java.util.stream.Collectors
import jp.toastkid.yobidashi4.domain.model.aggregation.OutgoAggregationResult
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import kotlin.io.path.nameWithoutExtension

internal class OutgoCalculationBehavior(
    private val articlesReaderService: ArticlesReaderService,
    private val additionalLineFilter: (String) -> Boolean
) {

    operator fun invoke(keyword: String): OutgoAggregationResult {
        val aggregationResult = OutgoAggregationResult(keyword)
        articlesReaderService.invoke()
            .parallel()
            .map { it.nameWithoutExtension to Files.readAllLines(it) }
            .filter { it.first.startsWith(keyword) }
            .map {
                var isOutGoLine = false
                for (line in it.second) {
                    if (line.startsWith("#") && line.endsWith(SECTION_TITLE)) {
                        isOutGoLine = true
                    }

                    if (!isOutGoLine || !line.startsWith("|") || additionalLineFilter(line)) {
                        continue
                    }

                    val items = line.split("|")
                    val target = items[2]
                    if (target.endsWith(YEN_UNIT)) {
                        val priceStr = target.substring(0, target.indexOf(YEN_UNIT)).trim().replace(",", "")
                        if (priceStr.isNotBlank()) {
                            aggregationResult.add(it.first, items[0] + items[1].trim(), Integer.parseInt(priceStr))
                        }
                    }
                }
            }
            .collect(Collectors.toList())
        return aggregationResult
    }

}

private const val SECTION_TITLE = "家計簿"

private const val TARGET_LINE_LABEL = "(外食)"

private const val YEN_UNIT = "円"
