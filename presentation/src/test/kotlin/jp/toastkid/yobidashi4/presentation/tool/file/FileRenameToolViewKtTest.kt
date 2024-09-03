package jp.toastkid.yobidashi4.presentation.tool.file

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import java.nio.file.Path
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FileRenameToolViewKtTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(FileRenameToolViewModel::class)
        val element = mockk<Path>()
        every { element.fileName } returns element
        every { element.toString() } returns "test"
        every { anyConstructed<FileRenameToolViewModel>().items() } returns listOf(element)
        coEvery { anyConstructed<FileRenameToolViewModel>().collectDroppedPaths(any()) } just Runs
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
        }
    }
}