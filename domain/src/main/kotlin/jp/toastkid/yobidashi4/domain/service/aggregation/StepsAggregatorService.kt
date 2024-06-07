package jp.toastkid.yobidashi4.domain.service.aggregation

import java.nio.file.Files
import java.util.regex.Pattern
import jp.toastkid.yobidashi4.domain.model.aggregation.StepsAggregationResult
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import kotlin.io.path.nameWithoutExtension

class StepsAggregatorService(private val articlesReaderService: ArticlesReaderService) : ArticleAggregator {

    override operator fun invoke(keyword: String): StepsAggregationResult {
        val aggregationResult = StepsAggregationResult()
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
                                matcher.group(INDEX_STEPS).replace(",", "").toIntOrNull() ?: 0,
                                matcher.group(INDEX_CALORIE).toIntOrNull() ?: 0
                            )
                        }
                    }
            }
        return aggregationResult
    }

    override fun label() = "Steps"

    override fun iconPath() = "images/icon/ic_payments.xml"

    companion object {

        private const val TARGET = "今日の歩数は"

        private const val INDEX_STEPS = 1

        private const val INDEX_CALORIE = 2

        private val pattern = Pattern.compile("歩数は(.+?)、消費カロリーは(.+?)kcal")

    }

}