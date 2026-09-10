package jp.toastkid.yobidashi4.presentation.input

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.v2.runComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.input.InputHistory
import jp.toastkid.yobidashi4.domain.model.tab.InputHistoryTab
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InputHistoryViewKtTest {

    @RelaxedMockK
    private lateinit var viewModel: InputHistoryViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { viewModel.open(any()) } just Runs
        every { viewModel.openOnBackground(any()) } just Runs
        every { viewModel.onDispose(any()) } just Runs
        every { viewModel.delete(any()) } just Runs
        every { viewModel.dateTimeString(any()) } returns "test"
        every { viewModel.listState() } returns LazyListState()
        every { viewModel.items() } returns listOf(
            InputHistory("test", 1),
            InputHistory("test2", 2),
        )
        coEvery { viewModel.launch(any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun test() {
        runComposeUiTest {
            setContent {
                InputHistoryView(InputHistoryTab("test"), viewModel)
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

            verify { viewModel.open(any()) }
            verify { viewModel.openOnBackground(any()) }
            verify { viewModel.delete(any()) }
            verify { viewModel.dateTimeString(any()) }
            verify { viewModel.items() }
            coVerify { viewModel.launch(any()) }
        }
    }
}