package jp.toastkid.yobidashi4.presentation.web.history

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.tab.WebHistoryTab
import jp.toastkid.yobidashi4.domain.model.web.history.WebHistory
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.domain.repository.web.history.WebHistoryRepository
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
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
        val tab = mockk<WebHistoryTab>()
        every { tab.scrollPosition() } returns 0

        subject.launch(CoroutineScope(Dispatchers.Unconfined), tab)

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

    @Test
    fun delete() {
        every { repository.delete(any()) } just Runs
        val webHistory = mockk<WebHistory>()
        every { webHistory.title } returns "test"
        every { webHistory.url } returns "test"
        every { webHistory.lastVisitedTime } returns -1
        every { webHistory.visitingCount } returns 1
        every { repository.readAll() } returns listOf(
            WebHistory(
                "test",
                "test",
                1697462064796
            ),
            webHistory
        )

        subject.delete(webHistory)

        verify { repository.delete(webHistory) }
    }

    @Test
    fun dropdown() {
        val item = mockk<WebHistory>()
        assertFalse(subject.openingDropdown(item))

        subject.openDropdown(item)

        assertTrue(subject.openingDropdown(item))
        assertFalse(subject.openingDropdown(mockk()))

        subject.closeDropdown()

        assertFalse(subject.openingDropdown(item))
        assertFalse(subject.openingDropdown(mockk()))
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun onPointerEvent() {
        val webHistory = mockk<WebHistory>()
        val pointerInputChange = mockk<PointerInputChange>()
        every { pointerInputChange.previousPressed } returns false
        every { pointerInputChange.pressed } returns true
        every { pointerInputChange.changedToDownIgnoreConsumed() } returns true
        val pointerEvent = spyk(PointerEvent(listOf(pointerInputChange)))
        every { pointerEvent.button } returns PointerButton.Secondary

        subject.onPointerEvent(pointerEvent, webHistory)

        assertTrue(subject.openingDropdown(webHistory))
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun noopOnPointerEvent() {
        val webHistory = mockk<WebHistory>()
        val pointerInputChange = mockk<PointerInputChange>()
        every { pointerInputChange.previousPressed } returns false
        every { pointerInputChange.pressed } returns true
        every { pointerInputChange.changedToDownIgnoreConsumed() } returns false
        val pointerEvent = spyk(PointerEvent(listOf(pointerInputChange)))
        every { pointerEvent.button } returns PointerButton.Secondary

        subject.onPointerEvent(pointerEvent, webHistory)

        assertFalse(subject.openingDropdown(webHistory))
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun noopOnPointerEventOnOpeningDropdown() {
        val bookmark = mockk<WebHistory>()
        val pointerInputChange = mockk<PointerInputChange>()
        every { pointerInputChange.previousPressed } returns false
        every { pointerInputChange.pressed } returns true
        every { pointerInputChange.changedToDownIgnoreConsumed() } returns true
        val pointerEvent = spyk(PointerEvent(listOf(pointerInputChange)))
        every { pointerEvent.button } returns PointerButton.Secondary
        subject.openDropdown(bookmark)

        subject.onPointerEvent(pointerEvent, bookmark)

        assertTrue(subject.openingDropdown(bookmark))
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun noopOnPointerEventWithOtherButton() {
        val bookmark = mockk<WebHistory>()
        val pointerInputChange = mockk<PointerInputChange>()
        every { pointerInputChange.previousPressed } returns false
        every { pointerInputChange.pressed } returns true
        every { pointerInputChange.changedToDownIgnoreConsumed() } returns true
        val pointerEvent = spyk(PointerEvent(listOf(pointerInputChange)))
        every { pointerEvent.button } returns PointerButton.Primary

        subject.onPointerEvent(pointerEvent, bookmark)

        assertFalse(subject.openingDropdown(bookmark))
    }

    @Test
    fun clear() {
        every { repository.clear() } just Runs
        every { repository.storeAll(any()) } just Runs
        every { repository.readAll() } returns listOf()
        val slot = slot<() -> Unit>()
        every { viewModel.showSnackbar(any(), any(), capture(slot)) } just Runs

        subject.clear()
        slot.captured.invoke()

        verify { repository.clear() }
        verify { viewModel.showSnackbar(any(), any(), any()) }
        verify { repository.storeAll(any()) }
        verify { repository.readAll() }
    }

    @Test
    fun clipText() {
        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs

        subject.clipText("test")

        verify { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun onDispose() {
        every { viewModel.updateScrollableTab(any(), any()) } just Runs
        val tab = mockk<WebHistoryTab>()

        subject.onDispose(tab)

        verify { viewModel.updateScrollableTab(tab, any()) }
    }

}