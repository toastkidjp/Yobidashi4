package jp.toastkid.yobidashi4.domain.service.archive

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import java.util.stream.Stream
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class KeywordSearch : KoinComponent {

    private val setting: Setting by inject()

    operator fun invoke(keyword: String, fileFilter: String?, files: Stream<Path> = Files.list(Paths.get(setting.articleFolder()))): MutableList<String> {
        return files
            .parallel()
            .map { it.toFile().nameWithoutExtension to Files.readAllLines(it) }
            .filter { filterByKeyword(fileFilter, it, keyword) }
            .map { it.first }
            .collect(Collectors.toList())
    }

    private fun filterByKeyword(fileFilter: String?, it: Pair<String, MutableList<String>>, keyword: String): Boolean {
        if (fileFilter?.isNotBlank() == true && !it.first.contains(fileFilter)) {
            return false
        }
        return it.second.any { line -> line.contains(keyword) }
    }
}
