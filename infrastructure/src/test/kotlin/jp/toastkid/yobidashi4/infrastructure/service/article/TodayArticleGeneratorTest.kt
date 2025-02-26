package jp.toastkid.yobidashi4.infrastructure.service.article

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.article.Article
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.article.ArticleTemplate
import jp.toastkid.yobidashi4.domain.service.article.ArticleTitleGenerator
import jp.toastkid.yobidashi4.domain.service.article.OffDayFinderService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import java.nio.file.Files
import java.nio.file.Path

class TodayArticleGeneratorTest {

    @InjectMockKs
    private lateinit var todayArticleGenerator: TodayArticleGeneratorImplementation

    @MockK
    private lateinit var articleFactory: ArticleFactory

    @MockK
    private lateinit var setting: Setting

    @MockK
    private lateinit var articleFolder: Path

    @MockK
    private lateinit var path: Path

    @MockK
    private lateinit var article: Article

    @MockK
    private lateinit var offDayFinderService: OffDayFinderService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { articleFactory } bind(ArticleFactory::class)
                    single(qualifier=null) { setting } bind(Setting::class)
                    single(qualifier=null) { offDayFinderService } bind(OffDayFinderService::class)
                }
            )
        }

        every { setting.articleFolderPath() }.returns(articleFolder)
        every { articleFolder.resolve(any<String>()) }.returns(path)
        every { articleFactory.withTitle(any()) }.returns(article)
        every { article.getTitle() } returns "title"
        every { article.makeFile(any()) }.just(Runs)
        every { offDayFinderService.invoke(any(), any(), any(), any(), any()) } returns false

        mockkStatic(Files::class)
        every { Files.exists(articleFolder) }.returns(true)
        every { Files.exists(path) }.returns(false)

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
        val slot = slot<() -> String>()
        every { article.makeFile(capture(slot)) }.just(Runs)
        mockkConstructor(ArticleTemplate::class)
        every { anyConstructed<ArticleTemplate>().invoke(any()) } returns "test"

        todayArticleGenerator.invoke()
        slot.captured.invoke()

        verify { articleFactory.withTitle(any()) }
        verify { article.makeFile(any()) }
        verify { anyConstructed<ArticleTemplate>().invoke(any()) }
    }

    @Test
    fun titleNull() {
        every { anyConstructed<ArticleTitleGenerator>().invoke(any()) }.returns(null)

        todayArticleGenerator.invoke()

        verify(inverse = true) { Files.exists(path) }
    }

    @Test
    fun existsCase() {
        every { Files.exists(path) }.returns(true)

        todayArticleGenerator.invoke()

        verify(inverse = true) { articleFactory.withTitle(any()) }
        verify(inverse = true) { article.makeFile(any()) }
    }

    @Test
    fun articleFolderDoesNotExistsCase() {
        every { Files.exists(articleFolder) }.returns(false)
        every { Files.exists(path) }.returns(true)

        todayArticleGenerator.invoke()

        verify(inverse = true) { anyConstructed<ArticleTitleGenerator>().invoke(any()) }
        verify(inverse = true) { articleFactory.withTitle(any()) }
        verify(inverse = true) { article.makeFile(any()) }
    }
}