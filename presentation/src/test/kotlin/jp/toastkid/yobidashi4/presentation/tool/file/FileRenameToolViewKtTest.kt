package jp.toastkid.yobidashi4.presentation.tool.file

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path

class FileRenameToolViewKtTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(FileRenameToolViewModel::class)
        val element = mockk<Path>()
        every { element.fileName } returns element
        every { element.toString() } returns "test"
        every { anyConstructed<FileRenameToolViewModel>().items() } returns listOf(element)
        every { anyConstructed<FileRenameToolViewModel>().dispose() } just Runs
        every { anyConstructed<FileRenameToolViewModel>().remove(any()) } just Runs
        coEvery { anyConstructed<FileRenameToolViewModel>().collectDroppedPaths() } just Runs
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
                FileRenameToolView()
            }

            onNodeWithText("x").performClick()
            verify { anyConstructed<FileRenameToolViewModel>().remove(any()) }
        }
    }
}