package jp.toastkid.yobidashi4.domain.service.archive

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.aggregation.FindResult
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class KeywordSearch : KoinComponent {

    private val setting: Setting by inject()

    operator fun invoke(keyword: String, fileFilter: String?, files: Stream<Path> = Files.list(Paths.get(setting.articleFolder()))): AggregationResult {
        val aggregationResult = FindResult(keyword)

        val actualKeywords = mutableSetOf<String>()

        if (keyword.contains(" ")) {
            keyword.split(" ").forEach { actualKeywords.add(it) }
        } else {
            actualKeywords.add(keyword)
        }

        files
            .parallel()
            .filter { fileFilter.isNullOrBlank() || it.toFile().nameWithoutExtension.contains(fileFilter) }
            .forEach {
                val lines = Files.readAllLines(it)
                val filteredList = lines.filter { line -> actualKeywords.any { actualKeyword -> line.contains(actualKeyword) } }
                if (filteredList.isNotEmpty()) {
                    aggregationResult.add(it.toFile().nameWithoutExtension, filteredList)
                }
            }
        return aggregationResult
    }

    private fun filterByKeyword(fileFilter: String?, it: Pair<String, MutableList<String>>, keyword: String): Boolean {
        if (fileFilter?.isNotBlank() == true && !it.first.contains(fileFilter)) {
            return false
        }
        return it.second.any { line -> line.contains(keyword) }
    }
}
