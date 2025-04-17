package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MarkdownTabViewModelTest {

    private lateinit var subject: MarkdownTabViewModel

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

        every { mainViewModel.updateScrollableTab(any(), any()) } just Runs

        subject = MarkdownTabViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun launch() {
        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } just Runs

        runComposeUiTest {
            setContent {
                rememberCoroutineScope().launch {
                    subject.launch(2)
                }
            }
        }

        assertEquals(2, subject.scrollState().value)
        verify { focusRequester.requestFocus() }
    }

    @Test
    fun focusRequester() {
        assertNotNull(subject.focusRequester())
    }

    @Test
    fun onDispose() {
        subject.onDispose(mockk<MarkdownPreviewTab>())

        verify { mainViewModel.updateScrollableTab(any(), any()) }
    }
}