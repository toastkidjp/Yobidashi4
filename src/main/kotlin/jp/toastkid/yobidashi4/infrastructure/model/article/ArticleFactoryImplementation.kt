package jp.toastkid.yobidashi4.infrastructure.model.article

import java.nio.file.Paths
import jp.toastkid.yobidashi4.domain.model.article.Article
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class ArticleFactoryImplementation : KoinComponent, ArticleFactory {

    private val setting: Setting by inject()

    override fun withTitle(title: String): Article {
        val file = Paths.get(setting.articleFolder(), "$title.md")
        return Article(file)
    }

}