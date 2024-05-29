package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.flow.emptyFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class TextLineViewKtTest {

    /** For just passing constructor. */
    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { mainViewModel.finderFlow() } returns emptyFlow()

        mockkConstructor(TextLineViewModel::class)
        coEvery { anyConstructed<TextLineViewModel>().launch(any()) } just Runs
        every { anyConstructed<TextLineViewModel>().annotatedString() } returns AnnotatedString("test-text-line")
        every { anyConstructed<TextLineViewModel>().onPointerReleased(any()) } just Runs
        every { anyConstructed<TextLineViewModel>().putLayoutResult(any()) } just Runs

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind(MainViewModel::class)
                }
            )
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun textLineView() {
        runDesktopComposeUiTest {
            setContent {
                TextLineView(
                    "test",
                    TextStyle(),
                    Modifier
                )
            }

            onNode(hasText("test-text-line"), useUnmergedTree = true).performMouseInput {
                press()
                release()
            }
            verify { anyConstructed<TextLineViewModel>().onPointerReleased(any()) }
        }
    }

}