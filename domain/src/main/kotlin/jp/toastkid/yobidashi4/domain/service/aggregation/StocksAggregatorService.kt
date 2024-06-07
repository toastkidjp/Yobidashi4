package jp.toastkid.yobidashi4.domain.service.aggregation

import java.nio.file.Files
import java.util.regex.Pattern
import jp.toastkid.yobidashi4.domain.model.aggregation.StocksAggregationResult
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import kotlin.io.path.nameWithoutExtension

class StocksAggregatorService(private val articlesReaderService: ArticlesReaderService) : ArticleAggregator {

    override operator fun invoke(keyword: String): StocksAggregationResult {
        val aggregationResult = StocksAggregationResult()
        articlesReaderService.invoke()
            .parallel()
            .filter { it.nameWithoutExtension.startsWith(keyword) }
            .map { it.nameWithoutExtension to Files.readAllLines(it) }
            .forEach {
                it.second.filter { line -> line.contains(TARGET) }
                    .forEach { line ->
                        val matcher = pattern.matcher(line)
                        while (matcher.find()) {
                            aggregationResult.put(
                                it.first,
                                matcher.group(INDEX_VALUATION).replace(",", "").toIntOrNull() ?: 0,
                                matcher.group(INDEX_GAIN_OR_LOSS).replace(",", "").toIntOrNull() ?: 0,
                                matcher.group(INDEX_PERCENT).replace("+", "").toDoubleOrNull() ?: 0.0
                            )
                        }
                    }
            }
        return aggregationResult
    }

    override fun label() = "Stock"

    override fun iconPath() = "images/icon/ic_asset_management.xml"

    companion object {

        private const val TARGET = "評価額は"

        private const val INDEX_VALUATION = 1

        private const val INDEX_GAIN_OR_LOSS = 2

        private const val INDEX_PERCENT = 3

        private val pattern = Pattern.compile("評価額は(.+?)円、評価損益は(.+?)円\\((.+?)%\\)だった。")

    }

}