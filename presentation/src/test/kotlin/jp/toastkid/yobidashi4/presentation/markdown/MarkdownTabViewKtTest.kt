package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.domain.service.markdown.MarkdownParser
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

class MarkdownTabViewKtTest {

    @RelaxedMockK
    private lateinit var viewModel: MarkdownTabViewModel

    @MockK
    private lateinit var path: Path

    @MockK
    private lateinit var tab: MarkdownPreviewTab

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { path.nameWithoutExtension } returns "test.md"

        mockkStatic(Files::class)
        every { Files.lines(any()) } returns """""".split("\n").stream()
        every { viewModel.scrollState() } returns LazyListState()
        every { viewModel.onDispose(any()) } just Runs
        every { tab.markdown() } returns MarkdownParser().invoke(path)
        every { tab.scrollPosition() } returns 0
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun markdownTabView() {
        runDesktopComposeUiTest {
            setContent {
                MarkdownTabView(tab, Modifier, viewModel)
            }
        }
    }
}