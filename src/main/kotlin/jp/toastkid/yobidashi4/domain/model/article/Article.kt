package jp.toastkid.yobidashi4.domain.model.article

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import kotlin.io.path.nameWithoutExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class Article(private val file: Path) {

    private val title = file.nameWithoutExtension

    fun getTitle() = title

    fun open() {
        /*try {
            OpenEditorService().invoke(this)
        } catch (e: IOException) {
            e.printStackTrace()
        }*/
    }

    fun makeFile(contentSupplier: () -> String) {
        Files.createFile(file)
        Files.write(file, contentSupplier().toByteArray(StandardCharsets.UTF_8))
    }

    fun count(): Int {
        return Files.readAllLines(file).map { it.codePointCount(0, it.length) }.sum()
    }

    fun lastModified(): Long {
        return try {
            Files.getLastModifiedTime(file).toMillis()
        } catch (e: IOException) {
            0L
        }
    }

    fun path() = file

    companion object : KoinComponent {

        private val setting: Setting by inject()

        fun withTitle(title: String): Article {
            val file = Paths.get(setting.articleFolder(), "$title.md")
            return Article(file)
        }
    }
}
