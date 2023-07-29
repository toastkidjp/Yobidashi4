package jp.toastkid.yobidashi4.infrastructure.service.article

import java.util.regex.Pattern
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.domain.service.article.ArticleOpener
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class ArticleOpenerImplementation : KoinComponent, ArticleOpener {

    private val viewModel: MainViewModel by inject()

    private val articleFactory: ArticleFactory by inject()

    override fun fromRawText(rawText: String?) {
        if (rawText.isNullOrBlank()) {
            return
        }
        val matcher = Pattern.compile("\\[\\[(.+?)\\]\\]", Pattern.DOTALL).matcher(rawText)
        while (matcher.find()) {
            viewModel.openFile(articleFactory.withTitle(matcher.group(1)).path())
        }
    }

}