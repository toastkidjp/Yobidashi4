package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.aggregation.StepsAggregationResult
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
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

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier=null) { articlesReaderService } bind(ArticlesReaderService::class)
                }
            )
        }

        every { mainViewModel.initialAggregationType() } returns 0

        mockkConstructor(AggregationBoxViewModel::class)
        every { anyConstructed<AggregationBoxViewModel>().isCurrentSwingContent() } returns false
        every { anyConstructed<AggregationBoxViewModel>().isOpeningChooser() } returns true
        every { anyConstructed<AggregationBoxViewModel>().selectedCategoryName() } returns "test"
        every { anyConstructed<AggregationBoxViewModel>().showAggregationBox() } returns true
        every { anyConstructed<AggregationBoxViewModel>().start() } just Runs
        every { anyConstructed<AggregationBoxViewModel>().categories() } returns mapOf(
            "test" to { StepsAggregationResult() },
            "test2" to { StepsAggregationResult() }
        )
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun aggregationBox() {
        runDesktopComposeUiTest {
            setContent {
                AggregationBox()
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun currentSwingContentCase() {
        every { anyConstructed<AggregationBoxViewModel>().isCurrentSwingContent() } returns true
        runDesktopComposeUiTest {
            setContent {
                AggregationBox()
            }
        }
    }

}