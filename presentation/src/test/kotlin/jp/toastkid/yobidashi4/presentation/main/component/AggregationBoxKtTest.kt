package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import java.util.stream.Stream
import jp.toastkid.yobidashi4.domain.service.aggregation.StepsAggregatorService
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import jp.toastkid.yobidashi4.domain.service.article.finder.FullTextArticleFinder
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_aggregation
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class AggregationBoxKtTest {

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var articlesReaderService: ArticlesReaderService

    @MockK
    private lateinit var fullTextArticleFinder: FullTextArticleFinder

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier=null) { articlesReaderService } bind(ArticlesReaderService::class)
                    single(qualifier=null) { fullTextArticleFinder } bind(FullTextArticleFinder::class)
                }
            )
        }

        every { mainViewModel.initialAggregationType() } returns 0

        mockkConstructor(AggregationBoxViewModel::class)
        every { anyConstructed<AggregationBoxViewModel>().isCurrentSwingContent() } returns false
        every { anyConstructed<AggregationBoxViewModel>().isOpeningChooser() } returns false
        every { anyConstructed<AggregationBoxViewModel>().selectedCategoryName() } returns "test"
        every { anyConstructed<AggregationBoxViewModel>().showAggregationBox() } returns true
        every { anyConstructed<AggregationBoxViewModel>().start() } just Runs
        every { anyConstructed<AggregationBoxViewModel>().choose(any()) } just Runs
        every { anyConstructed<AggregationBoxViewModel>().closeChooser() } just Runs
        every { anyConstructed<AggregationBoxViewModel>().onSearch() } just Runs
        every { anyConstructed<AggregationBoxViewModel>().switchAggregationBox(any()) } just Runs
        every { anyConstructed<AggregationBoxViewModel>().categories() } returns listOf(
            StepsAggregatorService(articlesReaderService)
        )
        every { articlesReaderService.invoke() } returns Stream.empty()
        every { fullTextArticleFinder.label() } returns "Find article"
        every { anyConstructed<AggregationBoxViewModel>().icon(any()) } returns Res.drawable.ic_aggregation
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun aggregationBox() {
        val label = "test input label"
        every { anyConstructed<AggregationBoxViewModel>().dateInput() } returns TextFieldValue(label)
        every { anyConstructed<AggregationBoxViewModel>().onDateInputValueChange(any()) } just Runs

        runDesktopComposeUiTest {
            setContent {
                AggregationBox()
            }

            val closeButton = onNode(hasText("x"), true)
            closeButton.assertExists()
            closeButton.performClick()
            verify { anyConstructed<AggregationBoxViewModel>().switchAggregationBox(any()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun withDropdown() {
        every { anyConstructed<AggregationBoxViewModel>().isOpeningChooser() } returns true
        val label = "test input label"
        every { anyConstructed<AggregationBoxViewModel>().dateInput() } returns TextFieldValue(label)
        every { anyConstructed<AggregationBoxViewModel>().onDateInputValueChange(any()) } just Runs

        runDesktopComposeUiTest {
            setContent {
                AggregationBox()
            }

            onNode(hasText(StepsAggregatorService(articlesReaderService).label()), true).onParent().performClick()
            verify { anyConstructed<AggregationBoxViewModel>().choose(any()) }

            val input = onNode(hasText(label), true)
            input.performClick()
            verify { anyConstructed<AggregationBoxViewModel>().closeChooser() }
            input.performTextInput("test")
            input.performImeAction()
            verify { anyConstructed<AggregationBoxViewModel>().onDateInputValueChange(any()) }
            verify { anyConstructed<AggregationBoxViewModel>().onSearch() }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun currentSwingContentCase() {
        every { anyConstructed<AggregationBoxViewModel>().isOpeningChooser() } returns true
        every { anyConstructed<AggregationBoxViewModel>().isCurrentSwingContent() } returns true
        runDesktopComposeUiTest {
            setContent {
                AggregationBox()
            }

            onNodeWithContentDescription(StepsAggregatorService(articlesReaderService).label(), true).performClick()
            verify { anyConstructed<AggregationBoxViewModel>().choose(any()) }
        }
    }

}