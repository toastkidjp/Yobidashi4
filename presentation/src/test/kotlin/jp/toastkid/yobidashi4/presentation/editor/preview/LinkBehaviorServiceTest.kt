package jp.toastkid.yobidashi4.presentation.editor.preview

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class LinkBehaviorServiceTest {

    @InjectMockKs
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

        every { internalLinkScheme.isInternalLink(any()) }.returns(true)
        every { internalLinkScheme.extract(any()) }.returns("yahoo")
        every { viewModel.openUrl(any(), any()) } just Runs
        every { viewModel.openFile(any()) } just Runs
        every { viewModel.showSnackbar(any()) } just Runs
    }

    @Test
    fun testNullUrl() {
        linkBehaviorService.invoke(null)
    }

    @Test
    fun testEmptyUrl() {
        linkBehaviorService.invoke("")
    }

    @Test
    fun testWebUrl() {
        every { internalLinkScheme.isInternalLink(any()) }.returns(false)

        linkBehaviorService.invoke("https://www.yahoo.co.jp")
    }

    @Test
    fun testArticleUrlDoesNotExists() {
        coEvery { exists(any()) }.answers { false }

        linkBehaviorService.invoke("internal-article://yahoo")
    }

    @Test
    fun testArticleUrl() {
        coEvery { exists(any()) }.answers { true }

        linkBehaviorService.invoke("internal-article://yahoo")
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

}