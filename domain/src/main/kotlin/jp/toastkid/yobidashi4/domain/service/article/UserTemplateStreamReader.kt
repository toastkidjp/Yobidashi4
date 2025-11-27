package jp.toastkid.yobidashi4.domain.service.article

import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class UserTemplateStreamReader {

    private val userTemplatePath = Path.of("user/article_template.txt")

    operator fun invoke(): InputStream? {
        return if (Files.exists(userTemplatePath)) {
            Files.newInputStream(userTemplatePath)
        } else {
            "# {{title}}\n".byteInputStream()
        }
    }
}