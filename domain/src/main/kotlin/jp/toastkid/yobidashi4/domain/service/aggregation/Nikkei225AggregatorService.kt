package jp.toastkid.yobidashi4.domain.service.aggregation

import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.aggregation.Nikkei225AggregationResult
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import kotlin.io.path.nameWithoutExtension

class Nikkei225AggregatorService(private val articlesReaderService: ArticlesReaderService) {

    operator fun invoke(keyword: String): Nikkei225AggregationResult {
        val result = Nikkei225AggregationResult()

        articlesReaderService.invoke()
                .parallel()
                .filter { it.nameWithoutExtension.startsWith(keyword) }
                .map { it.nameWithoutExtension to Files.readAllLines(it) }
                .forEach {
                    extract(result, it)
                }

        return result
    }

    private fun extract(result: Nikkei225AggregationResult, pair: Pair<String, MutableList<String>>) {
        var next = false
        pair.second.forEach { line ->
            if (line.endsWith(TARGET_SUFFIX)) {
                next = true
                return@forEach
            }
            if (next) {
                val split = line.split("円(")
                val target: String = split[0]
                if (target.isNotEmpty()) {
                    result.put(pair.first, target, split[1].let { it.substring(0, it.length - 1) })
                    return@forEach
                }
                return
            }
        }
    }

    companion object {

        private const val TARGET_SUFFIX = "今日の日経平均株価終値"

    }
}
