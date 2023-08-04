package jp.toastkid.yobidashi4.infrastructure.service.article

import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.article.ArticleTemplate
import jp.toastkid.yobidashi4.domain.service.article.ArticleTitleGenerator
import jp.toastkid.yobidashi4.domain.service.article.OffDayFinderService
import jp.toastkid.yobidashi4.domain.service.article.TodayArticleGenerator
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class TodayArticleGeneratorImplementation : KoinComponent, TodayArticleGenerator {

    private val setting: Setting by inject()

    private val articleFactory: ArticleFactory by inject()

    private val offDayFinderService: OffDayFinderService by inject()

    override operator fun invoke() {
        val title = ArticleTitleGenerator().invoke() ?: return
        val path = setting.articleFolderPath().resolve("${title}.md")
        if (Files.exists(path)) {
            return
        }

        val article = articleFactory.withTitle(title)
        article.makeFile { ArticleTemplate(offDayFinderService = offDayFinderService)(article.getTitle()) }
    }

}