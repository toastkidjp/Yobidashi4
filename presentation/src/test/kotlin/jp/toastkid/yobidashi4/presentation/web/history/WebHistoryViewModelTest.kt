package jp.toastkid.yobidashi4.presentation.web.history

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.web.history.WebHistory
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.domain.repository.web.history.WebHistoryRepository
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.absolutePathString
import kotlin.io.path.pathString
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

class WebHistoryViewModelTest {

    private lateinit var subject: WebHistoryViewModel

    @MockK
    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var repository: WebHistoryRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { viewModel } bind(MainViewModel::class)
                    single(qualifier = null) { repository } bind(WebHistoryRepository::class)
                }
            )
        }

        mockkConstructor(WebIcon::class)
        every { anyConstructed<WebIcon>().readAll() } returns listOf(mockk())

        subject = WebHistoryViewModel()
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
        assertTrue(subject.list().isEmpty())

        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } just Runs
        every { repository.readAll() } returns listOf(
            WebHistory(
                "test",
                "test",
                1697462064796
            ),
            WebHistory(
                "test",
                "test",
                1697462064797
            )
        )

        subject.launch()

        verify { repository.readAll() }
        assertEquals(2, subject.list().size)
    }

    @Test
    fun openUrl() {
        every { viewModel.openUrl(any(), any()) } just Runs

        subject.openUrl("https://www.yahoo.co.jp", true)

        verify { viewModel.openUrl(any(), any()) }
    }

    @Test
    fun findIconPathNotFoundCase() {
        subject.findIconPath(
            WebHistory(
                "test",
                "test",
                1697462064796
            )
        )
    }

    @Test
    fun findIconPath() {
        mockkConstructor(WebIcon::class)
        val path = mockk<Path>()
        every { path.fileName } returns path
        every { path.pathString } returns "www.test.co.jp.webp"
        every { anyConstructed<WebIcon>().readAll() } returns listOf(path)
        every { path.absolutePathString() } returns "OK"
        subject = WebHistoryViewModel()

        val result = subject.findIconPath(
            WebHistory(
                "test",
                "https://www.test.co.jp/index.html",
                1697462064796
            )
        )

        assertEquals("OK", result)
    }

    @Test
    fun dateTimeString() {
        assertEquals(
            "2023-10-16(Mon)22:14:24",
            subject.dateTimeString(
                WebHistory(
                    "test",
                    "test",
                    1697462064796
                )
            )
        )
    }

    @Test
    fun browseUri() {
        every { viewModel.browseUri(any()) } just Runs

        subject.browseUri("https://www.yahoo.co.jp")

        verify { viewModel.browseUri(any()) }
    }
}