package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.font.FontWeight
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.markdown.Markdown
import jp.toastkid.yobidashi4.domain.model.slideshow.data.Line
import jp.toastkid.yobidashi4.domain.service.markdown.MarkdownParser
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.flow.emptyFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MarkdownPreviewKtTest {

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkConstructor(MarkdownPreviewViewModel::class)
        every { anyConstructed<MarkdownPreviewViewModel>().makeFontWeight(any()) } returns FontWeight.Normal
        every { anyConstructed<MarkdownPreviewViewModel>().extractText(any(), any()) } returns "test"
        every { anyConstructed<MarkdownPreviewViewModel>().loadBitmap(any()) } returns ImageBitmap(1, 1)
        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind (MainViewModel::class)
                }
            )
        }
        every { mainViewModel.finderFlow() } returns emptyFlow()
        every { mainViewModel.putSecondaryClickItem(any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun test() {
        val mocked = mockk<Markdown>()
        val content = MarkdownParser().invoke(
            "> test\n![test link](https://www.yahoo.co.jp/favicon.ico)\ntest\n- 1st\n- 2nd\n```test```",
            "test"
        )
        every { mocked.lines() } returns content.lines().plus(mockk<Line>())

        runDesktopComposeUiTest {
            setContent {
                MarkdownPreview(
                    mocked,
                    rememberScrollState(),
                    Modifier
                )
            }

            onAllNodes(hasText("test"))
                .onFirst()
                .assertExists("Not found!")
                .performClick()
                .performKeyInput {
                    pressKey(Key.DirectionUp, 500L)
                    pressKey(Key.DirectionDown, 500L)
                    pressKey(Key.Enter, 500L)
                }
        }
    }

}