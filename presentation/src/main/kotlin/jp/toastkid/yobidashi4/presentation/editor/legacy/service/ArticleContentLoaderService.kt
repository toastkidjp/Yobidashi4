package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import java.nio.file.Files
import java.nio.file.Path

class ArticleContentLoaderService(private val lineSeparator: String = System.lineSeparator()) {

    operator fun invoke(path: Path): String {
        return Files.readAllLines(path).reduce { base, item -> "$base$lineSeparator$item" } + lineSeparator
    }

}