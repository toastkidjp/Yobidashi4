package jp.toastkid.yobidashi4.domain.service.article

import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TodayArticleGenerator {

    operator fun invoke() {
        val title = ArticleTitleGenerator().invoke() ?: return
        val koin =  object : KoinComponent {
            val setting: Setting by inject()
            val articleFactory: ArticleFactory by inject()
        }
        val path = koin.setting.articleFolderPath().resolve("${title}.md")
        if (Files.exists(path)) {
            return
        }

        val article = koin.articleFactory.withTitle(title)
        article.makeFile { ArticleTemplate()(article.getTitle()) }
    }

}