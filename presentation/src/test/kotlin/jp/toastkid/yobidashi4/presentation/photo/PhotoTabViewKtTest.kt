package jp.toastkid.yobidashi4.presentation.photo

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.toOffset
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.tab.PhotoTab
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_up
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path

class PhotoTabViewKtTest {

    @RelaxedMockK
    private lateinit var viewModel: PhotoTabViewModel

    private lateinit var tab: PhotoTab

    @MockK
    private lateinit var path: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { path.fileName } returns path
        every { path.toString() } returns "test.png"

        every { viewModel.bitmap() } returns ImageBitmap(1, 1)
        every { viewModel.handleIconPath() } returns Res.drawable.ic_up
        every { viewModel.launch(any()) } just Runs
        every { viewModel.visibleMenu() } returns true
        every { viewModel.scale() } returns 1f

        tab = PhotoTab(path)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun photoTabView() {
        every { viewModel.showHandle() } just Runs
        every { viewModel.hideHandle() } just Runs

        runDesktopComposeUiTest {
            setContent {
                PhotoTabView(tab, viewModel)
            }

            onNodeWithContentDescription("Switch menu", useUnmergedTree = true).performClick()
            onNodeWithContentDescription("Rotation left", useUnmergedTree = true).performClick()
            onNodeWithContentDescription("Rotation right", useUnmergedTree = true).performClick()
            onNodeWithContentDescription("Switch menu", useUnmergedTree = true).performClick()
                .performKeyInput {
                    pressKey(Key.DirectionUp, 1000L)
                }
                .performMouseInput {
                    enter()
                    exit()
                }
            verify { viewModel.showHandle() }
            verify { viewModel.hideHandle() }

            onNodeWithContentDescription("Divide GIF", useUnmergedTree = true).assertDoesNotExist()

            onNodeWithContentDescription("test.png", useUnmergedTree = true)
                .performMouseInput {
                    doubleClick()
                    press()
                    moveBy(IntOffset(20, 32).toOffset(), 100L)
                    release()
                }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun divideGif() {
        every { viewModel.divideGif(any()) } just Runs
        every { path.toString() } returns "test.gif"
        tab = PhotoTab(path)

        runDesktopComposeUiTest {
            setContent {
                PhotoTabView(tab, viewModel)
            }
            onNodeWithContentDescription("Switch menu", useUnmergedTree = true).performClick()
            onNodeWithContentDescription("Divide GIF", useUnmergedTree = true).performClick()

            verify { viewModel.divideGif(any()) }
        }
    }

}