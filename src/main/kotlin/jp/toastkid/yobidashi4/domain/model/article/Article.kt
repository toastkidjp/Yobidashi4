package jp.toastkid.yobidashi4.domain.model.article

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

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

}
