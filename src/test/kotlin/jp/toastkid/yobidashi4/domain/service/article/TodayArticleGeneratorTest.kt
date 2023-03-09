package jp.toastkid.yobidashi4.domain.service.article

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.article.Article
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class TodayArticleGeneratorTest {

    @InjectMockKs
    private lateinit var todayArticleGenerator: TodayArticleGenerator

    @MockK
    private lateinit var articleFactory: ArticleFactory

    @MockK
    private lateinit var setting: Setting

    @MockK
    private lateinit var path: Path

    @MockK
    private lateinit var article: Article

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { articleFactory } bind(ArticleFactory::class)
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }

        every { setting.articleFolderPath() }.returns(path)
        every { path.resolve(any<String>()) }.returns(path)
        every { articleFactory.withTitle(any()) }.returns(article)
        every { article.makeFile(any()) }.just(Runs)

        mockkStatic(Files::class)
        every { Files.exists(any()) }.returns(false)

        mockkConstructor(ArticleTitleGenerator::class)
        every { anyConstructed<ArticleTitleGenerator>().invoke(any()) }.returns("2023-03-08(Wed)")
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun invoke() {
        todayArticleGenerator.invoke()

        verify { articleFactory.withTitle(any()) }
        verify { article.makeFile(any()) }
    }

    @Test
    fun titleNull() {
        every { anyConstructed<ArticleTitleGenerator>().invoke(any()) }.returns(null)

        todayArticleGenerator.invoke()

        verify(inverse = true) { Files.exists(any()) }
    }

    @Test
    fun existsCase() {
        every { Files.exists(any()) }.returns(true)

        todayArticleGenerator.invoke()

        verify(inverse = true) { articleFactory.withTitle(any()) }
        verify(inverse = true) { article.makeFile(any()) }
    }
}