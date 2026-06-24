package jp.toastkid.yobidashi4.infrastructure.service.article

import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.article.ArticleTemplate
import jp.toastkid.yobidashi4.domain.service.article.ArticleTitleGenerator
import jp.toastkid.yobidashi4.domain.service.article.OffDayFinderService
import jp.toastkid.yobidashi4.domain.service.article.TodayArticleGenerator
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class TodayArticleGeneratorImplementation(
    private val fileSystem: FileSystem = FileSystem.SYSTEM
) : KoinComponent, TodayArticleGenerator {

    private val setting: Setting by inject()

    private val articleFactory: ArticleFactory by inject()

    private val offDayFinderService: OffDayFinderService by inject()

    override operator fun invoke() {
        if (fileSystem.exists(setting.articleFolderPath().toOkioPath()).not()) {
            return
        }

        val title = ArticleTitleGenerator().invoke() ?: return
        val path = setting.articleFolderPath().resolve("${title}.md")
        if (fileSystem.exists(path.toOkioPath())) {
            return
        }

        val article = articleFactory.withTitle(title)
        article.makeFile { ArticleTemplate(offDayFinderService = offDayFinderService)(article.getTitle()) }
    }

}