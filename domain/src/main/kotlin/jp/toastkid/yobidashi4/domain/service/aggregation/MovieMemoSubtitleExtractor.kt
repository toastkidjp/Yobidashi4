package jp.toastkid.yobidashi4.domain.service.aggregation

import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.aggregation.MovieMemoExtractorResult
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import kotlin.io.path.nameWithoutExtension
import kotlin.streams.asSequence

class MovieMemoSubtitleExtractor(private val articlesReaderService: ArticlesReaderService) {

    operator fun invoke(keyword: String): MovieMemoExtractorResult {
        val result = MovieMemoExtractorResult()
        articlesReaderService.invoke()
                .asSequence()
                .filter { it.fileName.toString().startsWith(keyword) }
                .map {
                    it.nameWithoutExtension to
                        Files.readAllLines(it)
                                .filter { line -> line.startsWith("##") && line.contains("年、") }
                                .map { line -> line.substring(line.indexOf(" ")).trim() }
                }
                .filter { it.second.isNotEmpty() }
                .forEach {
                   it.second.forEach { line -> result.add(it.first, line) }
                }
        return result
    }
}