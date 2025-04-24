package jp.toastkid.yobidashi4.presentation.input

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.input.InputHistory
import jp.toastkid.yobidashi4.domain.model.tab.InputHistoryTab
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import jp.toastkid.yobidashi4.domain.service.article.finder.FullTextArticleFinder
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class InputHistoryViewKtTest {

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var keywordSearch: FullTextArticleFinder

    @MockK
    private lateinit var articlesReaderService: ArticlesReaderService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind (MainViewModel::class)
                    single(qualifier = null) { keywordSearch } bind (FullTextArticleFinder::class)
                    single(qualifier = null) { articlesReaderService } bind (ArticlesReaderService::class)
                }
            )
        }

        mockkConstructor(InputHistoryViewModel::class)
        every { anyConstructed<InputHistoryViewModel>().open(any()) } just Runs
        every { anyConstructed<InputHistoryViewModel>().openOnBackground(any()) } just Runs
        every { anyConstructed<InputHistoryViewModel>().onDispose(any()) } just Runs
        every { anyConstructed<InputHistoryViewModel>().delete(any()) } just Runs
        every { anyConstructed<InputHistoryViewModel>().dateTimeString(any()) } returns "test"
        every { anyConstructed<InputHistoryViewModel>().items() } returns listOf(
            InputHistory("test", 1),
            InputHistory("test2", 2),
        )
        every { anyConstructed<InputHistoryViewModel>().launch(any(), any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun test() {
        runComposeUiTest {
            setContent {
                InputHistoryView(InputHistoryTab("test"))
            }

            onNodeWithText("test2", useUnmergedTree = true)
                .performClick()
                .performMouseInput {
                    enter()
                    longClick()
                    exit()
                }
            onNodeWithContentDescription("Delete item test", useUnmergedTree = true)
                .performClick()
        }
    }
}