package jp.toastkid.yobidashi4.domain.service.aggregation

import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.aggregation.ArticleLengthAggregationResult
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService

class ArticleLengthAggregatorService(
    private val articlesReaderService: ArticlesReaderService
) {

    operator fun invoke(keyword: String): ArticleLengthAggregationResult {
        val result = ArticleLengthAggregationResult()

        articlesReaderService.invoke()
                .parallel()
                .map { it.toFile().nameWithoutExtension to Files.readAllBytes(it) }
                .filter { it.first.startsWith(keyword) }
                .forEach {
                    result.put(it.first, String(it.second).trim().codePoints().count())
                }

        return result
    }

}
