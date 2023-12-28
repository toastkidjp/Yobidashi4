package jp.toastkid.yobidashi4.presentation.web.bookmark

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class WebBookmarkTabViewModelTest {

    private lateinit var subject: WebBookmarkTabViewModel

    @MockK
    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var repository: BookmarkRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { viewModel } bind(MainViewModel::class)
                    single(qualifier = null) { repository } bind(BookmarkRepository::class)
                }
            )
        }

        subject = WebBookmarkTabViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun listState() {
        assertNotNull(subject.listState())
    }

    @Test
    fun focusRequester() {
        assertNotNull(subject.focusRequester())
    }

    @Test
    fun scrollAction() {
        subject.scrollAction(CoroutineScope(Dispatchers.Unconfined), Key.DirectionUp, false)
    }
    @Test
    fun launch() {
        assertTrue(subject.bookmarks().isEmpty())

        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } just Runs
        every { repository.list() } returns listOf(mockk(), mockk())

        subject.launch()

        verify { repository.list() }
        assertEquals(2, subject.bookmarks().size)
    }

    @Test
    fun delete() {
        every { repository.delete(any()) } just Runs

        subject.delete(mockk())

        verify { repository.delete(any()) }
    }

    @Test
    fun openUrl() {
        every { viewModel.openUrl(any(), any()) } just Runs

        subject.openUrl("https://www.yahoo.co.jp", true)

        verify { viewModel.openUrl(any(), any()) }
    }

    @Test
    fun browseUri() {
        every { viewModel.browseUri(any()) } just Runs

        subject.browseUri("https://www.yahoo.co.jp")

        verify { viewModel.browseUri(any()) }
    }

}