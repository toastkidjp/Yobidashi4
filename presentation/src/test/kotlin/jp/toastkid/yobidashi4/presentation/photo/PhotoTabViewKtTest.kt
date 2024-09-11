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
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.tab.PhotoTab
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PhotoTabViewKtTest {

    private lateinit var tab: PhotoTab

    @MockK
    private lateinit var path: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { path.fileName } returns path
        every { path.toString() } returns "test.png"

        mockkConstructor(PhotoTabViewModel::class)
        every { anyConstructed<PhotoTabViewModel>().bitmap() } returns ImageBitmap(1, 1)
        every { anyConstructed<PhotoTabViewModel>().launch(any()) } just Runs

        tab = PhotoTab(path)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun photoTabView() {
        every { anyConstructed<PhotoTabViewModel>().showHandle() } just Runs
        every { anyConstructed<PhotoTabViewModel>().hideHandle() } just Runs

        runDesktopComposeUiTest {
            setContent {
                PhotoTabView(tab)
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
            verify { anyConstructed<PhotoTabViewModel>().showHandle() }
            verify { anyConstructed<PhotoTabViewModel>().hideHandle() }

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
        coEvery { anyConstructed<PhotoTabViewModel>().divideGif(any()) } just Runs
        every { path.toString() } returns "test.gif"
        tab = PhotoTab(path)

        runDesktopComposeUiTest {
            setContent {
                PhotoTabView(tab)
            }
            onNodeWithContentDescription("Switch menu", useUnmergedTree = true).performClick()
            onNodeWithContentDescription("Divide GIF", useUnmergedTree = true).performClick()

            coVerify { anyConstructed<PhotoTabViewModel>().divideGif(any()) }
        }
    }

}