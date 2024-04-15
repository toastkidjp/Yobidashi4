package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.font.FontWeight
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
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
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun test() {
        runDesktopComposeUiTest {
            setContent {
                MarkdownPreview(
                    MarkdownParser().invoke("> test\n![test](https://www.yahoo.co.jp/favicon.ico)\ntest", "test"),
                    rememberScrollState(),
                    Modifier
                )
            }
        }
    }

}