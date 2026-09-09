package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.aggregation.FindResult
import jp.toastkid.yobidashi4.domain.model.aggregation.StepsAggregationResult
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TableViewKtTest {

    @RelaxedMockK
    private lateinit var viewModel: TableViewModel

    private val scrollEventFlow = MutableSharedFlow<Float>(extraBufferCapacity = 1)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        
        every { viewModel.sort(any(), any()) } just Runs
        every { viewModel.openMarkdownPreview(any()) } just Runs
        every { viewModel.edit(any()) } just Runs
        every { viewModel.scrollEventFlow() } returns scrollEventFlow
        every { viewModel.listState() } returns LazyListState()
        every { viewModel.makeWeight(any()) } returns 0.4f
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tableView() {
        val result = StepsAggregationResult()
        result.put("2022-12-22", 1200, 12)
        result.put("2022-12-23", 1240, 12)
        result.put("2022-12-24", 1230, 12)
        every { viewModel.items() } returns mutableStateListOf<Array<Any>>().also { it.addAll(result.itemArrays()) }

        runDesktopComposeUiTest {
            setContent {
                TableView(TableTab("test", result), viewModel)
            }

            onNode(hasText("Steps"), useUnmergedTree = true).onParent().performMouseInput {
                enter()
                click()
                click()
                exit()
            }
            verify { viewModel.sort(any(), any()) }

            onNode(hasText("Steps"), useUnmergedTree = true).onParent().performMouseInput {
                click()
                click()
                click()
            }
                .performKeyInput {
                    pressKey(Key.DirectionUp, 1000L)
                }
            verify { viewModel.sort(any(), any()) }

            val previewButton = onAllNodesWithContentDescription("Open preview", useUnmergedTree = true).onFirst()
            previewButton.performClick()
            verify { viewModel.openMarkdownPreview(any()) }

            onAllNodesWithContentDescription("Open file", useUnmergedTree = true).onFirst().performClick()
            verify { viewModel.edit(any()) }

            verify { viewModel.scrollEventFlow() }
            scrollEventFlow.tryEmit(1f)
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun findResultCase() {
        runDesktopComposeUiTest {
            setContent {
                val result = FindResult("test")
                result.add("2022-12-22", listOf("1st", "2nd"))
                result.add("2022-12-22", listOf("1st", "2nd"))

                TableView(TableTab("test", result), viewModel)
            }
        }
    }
}