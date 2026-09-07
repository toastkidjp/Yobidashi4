package jp.toastkid.yobidashi4.presentation.tool.file

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path

class FileRenameToolViewKtTest {

    @RelaxedMockK
    private lateinit var viewModel: FileRenameToolViewModel
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        val element = mockk<Path>()
        every { element.fileName } returns element
        every { element.toString() } returns "test"
        every { viewModel.items() } returns listOf(element)
        every { viewModel.dispose() } just Runs
        every { viewModel.remove(any()) } just Runs
        coEvery { viewModel.collectDroppedPaths() } just Runs
        every { viewModel.renamedSampleFileName() } returns ""
        every { viewModel.useResize() } returns MutableStateFlow(false)
        every { viewModel.listState() } returns LazyListState()
        every { viewModel.input() } returns TextFieldState()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun fileRenameToolView() {
        runDesktopComposeUiTest {
            setContent {
                FileRenameToolView(viewModel)
            }

            onNodeWithText("x").performClick()
            verify { viewModel.remove(any()) }
        }
    }


    @OptIn(ExperimentalTestApi::class)
    @Test
    fun emptyCase() {
        every { viewModel.items() } returns emptyList()
        every { viewModel.input() } returns TextFieldState()

        runDesktopComposeUiTest {
            setContent {
                FileRenameToolView(viewModel)
            }

            onNodeWithText("x").assertDoesNotExist()
        }
    }

}