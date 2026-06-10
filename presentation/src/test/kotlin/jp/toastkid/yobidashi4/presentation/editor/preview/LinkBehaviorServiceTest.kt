package jp.toastkid.yobidashi4.presentation.editor.preview

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.article.Article
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class LinkBehaviorServiceTest {

    private lateinit var linkBehaviorService: LinkBehaviorService

    @MockK
    private lateinit var exists: (String) -> Boolean

    @MockK
    private lateinit var internalLinkScheme: InternalLinkScheme

    @Suppress("unused")
    private val mainDispatcher = Dispatchers.Unconfined

    @Suppress("unused")
    private val ioDispatcher = Dispatchers.Unconfined

    @MockK
    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var articleFactory: ArticleFactory

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single(qualifier = null) { viewModel } bind(MainViewModel::class)
                    single(qualifier = null) { articleFactory } bind(ArticleFactory::class)
                }
            )
        }

        MockKAnnotations.init(this)

        every { internalLinkScheme.isInternalLink(any()) } returns true
        every { internalLinkScheme.extract(any()) } returns "yahoo"
        every { viewModel.openUrl(any(), any()) } just Runs
        every { viewModel.edit(any()) } just Runs
        every { viewModel.showSnackbar(any()) } just Runs
        every { viewModel.editWithTitle(any(), any()) } just Runs
        every { viewModel.openPreview(any(), any()) } just Runs
        val article = mockk<Article>()
        every { articleFactory.withTitle(any()) } returns article
        every { article.path() } returns mockk()

        linkBehaviorService = LinkBehaviorService(exists, internalLinkScheme, mainDispatcher, ioDispatcher)
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun testNullUrl() {
        linkBehaviorService.invoke(null)

        verify { viewModel wasNot called }
    }

    @Test
    fun testEmptyUrl() {
        linkBehaviorService.invoke("")

        verify { viewModel wasNot called }
    }

    @Test
    fun testWebUrl() {
        every { internalLinkScheme.isInternalLink(any()) } returns false

        linkBehaviorService.invoke("https://www.yahoo.co.jp")

        verify { viewModel.openUrl("https://www.yahoo.co.jp", false) }
    }

    @Test
    fun testWebUrlOnBackground() {
        every { internalLinkScheme.isInternalLink(any()) } returns false

        linkBehaviorService.invoke("https://www.yahoo.co.jp", true)

        verify { viewModel.openUrl("https://www.yahoo.co.jp", true) }
    }

    @ParameterizedTest
    @CsvSource(
        "false",
        "true,",
    )
    fun testArticleUrl(returnsExists: Boolean) {
        every { exists(any()) } returns returnsExists

        linkBehaviorService.invoke("internal-article://yahoo")

        verify { exists(any()) }
        verify(inverse = !returnsExists) { articleFactory.withTitle(any()) }
        verify(inverse = !returnsExists) { viewModel.openPreview(any(), any()) }
        verify(inverse = returnsExists) { viewModel.showSnackbar(any()) }
    }

    @Test
    fun testArticleUrlWithDefaultExistsCallback() {
        linkBehaviorService = LinkBehaviorService(internalLinkScheme = internalLinkScheme, ioDispatcher = ioDispatcher, mainDispatcher = mainDispatcher)

        linkBehaviorService.invoke("internal-article://yahoo")

        verify { viewModel.openPreview(any(), any()) }
    }

}