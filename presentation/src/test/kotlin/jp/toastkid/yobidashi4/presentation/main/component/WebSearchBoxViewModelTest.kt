package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.search.SearchSite
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class WebSearchBoxViewModelTest {

    private lateinit var subject: WebSearchBoxViewModel

    @MockK
    private lateinit var viewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { viewModel } bind(MainViewModel::class)
                }
            )
        }

        subject = WebSearchBoxViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun setShowWebSearch() {
        every { viewModel.setShowWebSearch(any()) } just Runs

        subject.setShowWebSearch(true)

        verify { viewModel.setShowWebSearch(true) }
    }

    @Test
    fun openDropdown() {
        assertFalse(subject.openingDropdown())

        subject.setOpenDropdown()

        assertTrue(subject.openingDropdown())

        subject.closeDropdown()

        assertFalse(subject.openingDropdown())
    }

    @Test
    fun containsSwingContent() {
        every { viewModel.currentTab() } returns mockk()

        assertFalse(subject.containsSwingContent())
    }

    @Test
    fun choose() {
        subject.setOpenDropdown()

        subject.choose(SearchSite.SEARCH_WITH_IMAGE)

        assertEquals(SearchSite.SEARCH_WITH_IMAGE.iconPath(), subject.currentIconPath())
        assertEquals(SearchSite.SEARCH_WITH_IMAGE.siteName, subject.currentSiteName())
        assertFalse(subject.openingDropdown())
    }

    @Test
    fun onValueChange() {
        subject.onValueChange(TextFieldValue("1+2"))

        assertEquals("1+2", subject.query().text)
        assertEquals("3", subject.result())

        subject.clearInput()

        assertTrue(subject.query().text.isEmpty())
    }

    @Test
    fun invokeSearch() {
        every { viewModel.openUrl(any(), any()) } just Runs
        every { viewModel.setShowWebSearch(any()) } just Runs
        subject.onValueChange(TextFieldValue("test"))

        subject.invokeSearch()

        verify { viewModel.openUrl(any(), any()) }
        verify { viewModel.setShowWebSearch(false) }
    }

    @Test
    fun invokeSearchWithUrlInput() {
        every { viewModel.openUrl(any(), any()) } just Runs
        every { viewModel.setShowWebSearch(any()) } just Runs
        subject.onValueChange(TextFieldValue("https://www.yahoo.co.jp"))

        subject.invokeSearch()

        verify { viewModel.openUrl(any(), any()) }
        verify { viewModel.setShowWebSearch(false) }
    }

    @Test
    fun noopInvokeSearch() {
        every { viewModel.openUrl(any(), any()) } just Runs

        subject.invokeSearch()

        verify { viewModel wasNot called }
    }

    @Test
    fun noopInvokeSearchWithComposition() {
        every { viewModel.openUrl(any(), any()) } just Runs
        subject.onValueChange(TextFieldValue("test", composition = TextRange.Zero))

        subject.invokeSearch()

        verify { viewModel wasNot called }
    }

    @Test
    fun onKeyEvent() {
        every { viewModel.setShowWebSearch(any()) } just Runs

        val consumed = subject.onKeyEvent(
            KeyEvent(
                java.awt.event.KeyEvent(
                    mockk(),
                    java.awt.event.KeyEvent.KEY_PRESSED,
                    1,
                    -1,
                    java.awt.event.KeyEvent.VK_ESCAPE,
                    'A'
                )
            )
        )

        assertTrue(consumed)
        verify { viewModel.setShowWebSearch(false) }
    }

    @Test
    fun notConsumedOnKeyEvent() {
        every { viewModel.setShowWebSearch(any()) } just Runs

        val consumed = subject.onKeyEvent(
            KeyEvent(
                java.awt.event.KeyEvent(
                    mockk(),
                    java.awt.event.KeyEvent.KEY_PRESSED,
                    1,
                    java.awt.event.KeyEvent.CTRL_DOWN_MASK,
                    java.awt.event.KeyEvent.VK_UP,
                    'A'
                )
            )
        )

        assertFalse(consumed)
        verify { viewModel wasNot called }
    }

    @Test
    fun existsResult() {
        assertFalse(subject.existsResult())
    }

    @Test
    fun result() {
        assertTrue(subject.result().isEmpty())
    }

    @Test
    fun showWebSearch() {
        every { viewModel.showWebSearch() } returns true

        assertTrue(subject.showWebSearch())
    }

    @Test
    fun start() {
        every { viewModel.showWebSearch() } returns true
        val tab = mockk<WebTab>()
        every { viewModel.currentTab() } returns tab
        val url = "https://www.yahoo.co.jp"
        every { tab.url() } returns url
        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } just Runs

        subject.start()

        verify { focusRequester.requestFocus() }
        assertEquals(url, subject.query().text)
    }

    @Test
    fun startIfBoxIsClosed() {
        every { viewModel.showWebSearch() } returns false
        every { viewModel.currentTab() } returns mockk()
        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } just Runs

        subject.start()

        verify { focusRequester wasNot called }
        assertTrue(subject.query().text.isEmpty())
    }

}