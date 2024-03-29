package jp.toastkid.yobidashi4.infrastructure.service.article

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.article.Article
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class ArticleOpenerTest {

    @InjectMockKs
    private lateinit var articleOpener: ArticleOpenerImplementation

    @MockK
    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var articleFactory: ArticleFactory

    @MockK
    private lateinit var article: Article

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier = null) { viewModel } bind(MainViewModel::class)
                    single(qualifier = null) { articleFactory } bind(ArticleFactory::class)
                }
            )
        }

        every { viewModel.edit(any()) } just Runs
        every { articleFactory.withTitle(any()) } returns article
        every { article.path() } returns mockk()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun fromRawText() {
        articleOpener.fromRawText("[[テスト]]")

        verify(exactly = 1) { viewModel.edit(any()) }
        verify(exactly = 1) { articleFactory.withTitle(any()) }
    }

    @Test
    fun fromRawTextContainingPluralLinkCase() {
        articleOpener.fromRawText("今日はとにかく[[テスト]]して[[PDCA]]を回そう")

        verify(exactly = 2) { viewModel.edit(any()) }
        verify(exactly = 2) { articleFactory.withTitle(any()) }
    }

    @Test
    fun fromRawTextNoneLinkCase() {
        articleOpener.fromRawText("テスト")

        verify { viewModel wasNot called }
        verify { articleFactory wasNot called }
    }

    @Test
    fun blankCase() {
        articleOpener.fromRawText(" ")

        verify { viewModel wasNot called }
        verify { articleFactory wasNot called }
    }

    @Test
    fun emptyCase() {
        articleOpener.fromRawText("")

        verify { viewModel wasNot called }
        verify { articleFactory wasNot called }
    }

    @Test
    fun nullCase() {
        articleOpener.fromRawText(null)

        verify { viewModel wasNot called }
        verify { articleFactory wasNot called }
    }

}