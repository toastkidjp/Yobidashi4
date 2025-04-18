package jp.toastkid.yobidashi4.domain.service.aggregation

import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.aggregation.ArticleLengthAggregationResult
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import kotlin.io.path.nameWithoutExtension

class ArticleLengthAggregatorService(
    private val articlesReaderService: ArticlesReaderService
): ArticleAggregator {

    override operator fun invoke(keyword: String): AggregationResult {
        val result = ArticleLengthAggregationResult()

        articlesReaderService.invoke()
                .parallel()
                .filter { it.nameWithoutExtension.startsWith(keyword) }
                .map { it.nameWithoutExtension to Files.readAllBytes(it) }
                .forEach {
                    result.put(it.first, String(it.second).trim().codePoints().count())
                }

        return result
    }

    override fun label() = "Article length"

}
