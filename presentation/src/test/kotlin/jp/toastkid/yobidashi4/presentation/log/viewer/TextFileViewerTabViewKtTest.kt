package jp.toastkid.yobidashi4.presentation.log.viewer

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.tab.TextFileViewerTab
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TextFileViewerTabViewKtTest {

    @MockK
    private lateinit var path: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        every { Files.readAllLines(any()) } returns listOf("test", "test2")
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun test() {
        runDesktopComposeUiTest {
            setContent {
                TextFileViewerTabView(
                    TextFileViewerTab(path)
                )
            }
        }
    }
}