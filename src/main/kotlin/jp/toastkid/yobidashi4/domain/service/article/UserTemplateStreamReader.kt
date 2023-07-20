package jp.toastkid.yobidashi4.domain.service.article

import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class UserTemplateStreamReader {
    operator fun invoke(): InputStream? {
        val userTemplatePath = Path.of("user/article_template.txt")
        return if (Files.exists(userTemplatePath)) {
            Files.newInputStream(userTemplatePath)
        } else {
            javaClass.classLoader?.getResourceAsStream("article/template/article_template.txt")
        }
    }
}