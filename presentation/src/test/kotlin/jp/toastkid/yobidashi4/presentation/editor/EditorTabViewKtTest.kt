package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.markdown.Markdown
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.service.markdown.MarkdownParser
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class EditorTabViewKtTest {

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                }
            )
        }

        every { mainViewModel.darkMode() } returns false
        every { mainViewModel.updateEditorContent(any(), any(), any(), any(), any()) } just Runs
        every { mainViewModel.finderFlow() } returns emptyFlow()
        every { mainViewModel.setFindStatus(any()) } just Runs

        mockkConstructor(MarkdownParser::class)
        every { anyConstructed<MarkdownParser>().invoke(any()) } returns Markdown("test")
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun editorTabView() {
        runDesktopComposeUiTest {
            setContent {
                val tab = EditorTab(mockk())
                EditorTabView(tab)

                LaunchedEffect(Unit) {
                    tab.switchPreview()

                    delay(1200L)
                }
            }
        }
    }
}