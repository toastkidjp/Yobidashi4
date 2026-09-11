package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.input.InputHistory
import jp.toastkid.yobidashi4.domain.repository.input.InputHistoryRepository
import jp.toastkid.yobidashi4.domain.service.aggregation.ArticleAggregator
import jp.toastkid.yobidashi4.domain.service.aggregation.StepsAggregatorService
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_aggregation
import jp.toastkid.yobidashi4.library.resources.ic_search
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AggregationBoxKtTest {

    @RelaxedMockK
    private lateinit var viewModel: AggregationBoxViewModel
    
    @MockK
    private lateinit var repository: InputHistoryRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        
        every { viewModel.isCurrentSwingContent() } returns false
        every { viewModel.isOpeningChooser() } returns false
        every { viewModel.selectedCategoryName() } returns "test"
        every { viewModel.showAggregationBox() } returns true
        every { viewModel.start() } just Runs
        every { viewModel.choose(any()) } just Runs
        every { viewModel.closeChooser() } just Runs
        every { viewModel.onSearch() } just Runs
        every { viewModel.switchAggregationBox(any()) } just Runs
        every { viewModel.keyword() } returns TextFieldState()
        every { viewModel.dateInput() } returns TextFieldState()
        every { viewModel.focusingModifier() } returns Modifier
        every { viewModel.selectedCategoryIcon() } returns Res.drawable.ic_search
        every { viewModel.categories() } returns listOf(
            StepsAggregatorService(mockk())
        )
        every { viewModel.icon(any()) } returns Res.drawable.ic_aggregation
        val aggregator = mockk<ArticleAggregator>()
        every { aggregator.label() } returns "test"
        every { viewModel.items() } returns listOf(aggregator)
        every { repository.filter(any()) } returns listOf(InputHistory("test", 0))
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun aggregationBox() {
        val label = "test input label"
        every { viewModel.dateInput() } returns TextFieldState(label)
        every { viewModel.onDateInputValueChange() } just Runs

        runDesktopComposeUiTest {
            setContent {
                AggregationBox(viewModel)
            }

            val closeButton = onNode(hasText("x"), true)
            closeButton.assertExists()
            closeButton.performClick()
            verify { viewModel.switchAggregationBox(any()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun withDropdown() {
        every { viewModel.isOpeningChooser() } returns true
        val label = "test input label"
        every { viewModel.dateInput() } returns TextFieldState(label)
        every { viewModel.onDateInputValueChange() } just Runs

        runDesktopComposeUiTest {
            setContent {
                AggregationBox(viewModel)
            }

            // TODO verify { viewModel.choose(any()) }

            val input = onNode(hasText(label), true)
            input.performClick()
            verify { viewModel.closeChooser() }
            input.performImeAction()
            verify { viewModel.onDateInputValueChange() }
            verify { viewModel.onSearch() }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun currentSwingContentCase() {
        every { viewModel.isOpeningChooser() } returns true
        every { viewModel.isCurrentSwingContent() } returns true
        runDesktopComposeUiTest {
            setContent {
                AggregationBox(viewModel)
            }

            // TODO verify { viewModel.choose(any()) }
        }
    }

}