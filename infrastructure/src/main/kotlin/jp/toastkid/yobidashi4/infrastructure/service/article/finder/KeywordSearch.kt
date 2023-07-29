package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.aggregation.FindResult
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.archive.KeywordArticleFinder
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class KeywordSearch : KeywordArticleFinder, KoinComponent {

    private val setting: Setting by inject()

    override operator fun invoke(keyword: String, fileFilter: String?): AggregationResult {
        val files = Files.list(Path.of(setting.articleFolder()))

        val aggregationResult = FindResult(keyword)

        val filter = KeywordSearchFilter(keyword)

        files
            .parallel()
            .filter { fileFilter.isNullOrBlank() || it.toFile().nameWithoutExtension.contains(fileFilter) }
            .forEach {
                val lines = Files.readAllLines(it)
                val filteredList = lines.filter { line -> filter.invoke(line) }
                if (filteredList.isNotEmpty()) {
                    aggregationResult.add(it.toFile().nameWithoutExtension, filteredList)
                }
            }
        return aggregationResult
    }

}
