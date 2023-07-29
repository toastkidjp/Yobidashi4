package jp.toastkid.yobidashi4.domain.model.article

interface ArticleFactory {
    fun withTitle(title: String): Article
}