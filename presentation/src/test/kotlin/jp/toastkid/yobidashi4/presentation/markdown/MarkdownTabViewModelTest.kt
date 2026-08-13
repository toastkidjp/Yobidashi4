package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.test.ExperimentalTestApi
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
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
    fun launch() = runTest {
        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } returns true
        val listState = mockk<LazyListState>()
        coEvery { listState.scrollToItem(any()) } just Runs
        every { subject.scrollState() } returns listState

        launch(Dispatchers.Unconfined) {
            subject.launch(2)
        }
        verify { focusRequester.requestFocus() }
        coVerify { listState.scrollToItem(any()) }
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