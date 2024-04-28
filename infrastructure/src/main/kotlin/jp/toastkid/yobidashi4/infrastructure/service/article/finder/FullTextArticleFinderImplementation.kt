package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.aggregation.FindResult
import jp.toastkid.yobidashi4.domain.service.article.finder.FullTextArticleFinder
import kotlin.io.path.nameWithoutExtension
import org.koin.core.annotation.Single

@Single
class FullTextArticleFinderImplementation : FullTextArticleFinder {

    private val searcher = FullTextSearch.make(Path.of("temporary/finder/index"))

    override fun invoke(keyword: String, fileFilter: String?): AggregationResult {
        val aggregationResult = FindResult(keyword)

        val hits = searcher.search(keyword) ?: return aggregationResult

        val filter = KeywordSearchFilter(keyword)

        hits.scoreDocs
            .mapNotNull(searcher::getDocument)
            .filter { it["name"] != null }
            .parallelStream()
            .map { Path.of(it["path"]) }
            .filter { Files.isReadable(it) }
            .forEach {
                val lines = Files.readAllLines(it)
                val filteredList = lines.filter { line -> filter.invoke(line) }
                if (filteredList.isNotEmpty()) {
                    aggregationResult.add(it.nameWithoutExtension, filteredList)
                }
            }
        aggregationResult.sortByTitle()
        return aggregationResult
    }


}