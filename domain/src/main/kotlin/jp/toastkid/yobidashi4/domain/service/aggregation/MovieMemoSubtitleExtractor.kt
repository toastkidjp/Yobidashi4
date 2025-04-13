package jp.toastkid.yobidashi4.domain.service.aggregation

import jp.toastkid.yobidashi4.domain.model.aggregation.MovieMemoExtractorResult
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

class MovieMemoSubtitleExtractor(private val articlesReaderService: ArticlesReaderService) : ArticleAggregator {

    override operator fun invoke(keyword: String): MovieMemoExtractorResult {
        val result = MovieMemoExtractorResult()
        articlesReaderService.invoke()
                .parallel()
                .filter { it.fileName.toString().startsWith(keyword) }
                .map(::findMovieNames)
                .filter(::keepIsNotEmpty)
                .forEach {
                   it.second.forEach { line -> result.add(it.first, line) }
                }
        return result
    }

    private fun findMovieNames(it: Path) =
        it.nameWithoutExtension to
            Files.readAllLines(it)
                .filter { line -> line.startsWith("##") && line.contains("年、") }
                .map { line -> line.substring(line.indexOf(" ")).trim() }

    private fun keepIsNotEmpty(it: Pair<String, List<String>>) = it.second.isNotEmpty()

    override fun label() = "Movies"

}