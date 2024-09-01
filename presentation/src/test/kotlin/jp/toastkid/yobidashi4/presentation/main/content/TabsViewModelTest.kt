package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.unit.dp
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.unmockkConstructor
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import io.mockk.verify
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
import jp.toastkid.yobidashi4.domain.model.tab.FileRenameToolTab
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.repository.chat.ChatExporter
import jp.toastkid.yobidashi4.domain.service.table.TableContentExporter
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class TabsViewModelTest {
    
    private lateinit var subject: TabsViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var chatExporter: ChatExporter

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier = null) { chatExporter } bind(ChatExporter::class)
                }
            )
        }

        subject = TabsViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun tabIsEmpty() {
        every { mainViewModel.tabs } returns mutableListOf(mockk<Tab>())

        assertFalse(subject.tabIsEmpty())
    }

    @Test
    fun currentTabIndex() {
        every { mainViewModel.selected.value } returns 2

        assertEquals(0, subject.currentTabIndex(2))
    }

    @Test
    fun tabs() {
        every { mainViewModel.tabs } returns mockk()

        val tabs = subject.tabs()

        assertSame(tabs, mainViewModel.tabs)
        verify { mainViewModel.tabs }
    }

    @Test
    fun isSelectedIndex() {
        every { mainViewModel.selected.value } returns 0

        assertTrue(subject.isSelectedIndex(0))
        assertFalse(subject.isSelectedIndex(1))
    }

    @Test
    fun setSelectedIndex() {
        every { mainViewModel.setSelectedIndex(any()) } just Runs

        subject.setSelectedIndex(2)

        verify { mainViewModel.setSelectedIndex(any()) }
    }

    @Test
    fun currentTab() {
        val tab = mockk<Tab>()
        every { mainViewModel.currentTab() } returns tab

        val currentTab = subject.currentTab()

        assertSame(tab, currentTab)
    }

    @Test
    fun removeTabAt() {
        every { mainViewModel.removeTabAt(any()) } just Runs

        subject.removeTabAt(1)

        verify { mainViewModel.removeTabAt(any()) }
    }

    @Test
    fun exportTable() {
        mockkConstructor(TableContentExporter::class)
        every { anyConstructed<TableContentExporter>().invoke(any()) } just Runs
        mockkObject(TableContentExporter)
        every { TableContentExporter.exportTo() } returns "test"
        mockkStatic(Path::class)
        every { Path.of(any<String>()) } returns mockk()
        val slot = slot<() -> Unit>()
        every { mainViewModel.showSnackbar(any(), any(), capture(slot)) } just Runs
        every { mainViewModel.openFile(any()) } just Runs
        val tableItems = mockk<AggregationResult>()

        subject.exportTable(tableItems)
        slot.captured.invoke()

        verify { anyConstructed<TableContentExporter>().invoke(any()) }

        unmockkConstructor(TableContentExporter::class)
        unmockkObject(TableContentExporter)
        unmockkStatic(Path::class)
    }

    @Test
    fun exportChat() {
        val chatTab = mockk<ChatTab>()
        every { chatTab.chat() } returns mockk()
        every { chatExporter.invoke(any()) } just Runs

        subject.exportChat(chatTab)

        verify { chatTab.chat() }
        verify { chatExporter.invoke(any()) }
    }

    @Test
    fun closeOtherTabs() {
        every { mainViewModel.closeOtherTabs() } just Runs
        
        subject.closeOtherTabs()
        
        verify { mainViewModel.closeOtherTabs() }
    }

    @Test
    fun edit() {
        every { mainViewModel.edit(any(), any()) } just Runs
        
        subject.edit(mockk())
        
        verify { mainViewModel.edit(any(), any()) }
    }

    @Test
    fun openingDropdownMenu() {
        assertFalse(subject.openingDropdown(mockk()))
    }

    @Test
    fun calculateTabWidth() {
        assertEquals(1000.dp, subject.calculateTabWidth(mockk()))
        assertEquals(232.dp, subject.calculateTabWidth(mockk<WebTab>()))
    }

    @Test
    fun dropdown() {
        val item = mockk<Tab>()
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
        val webHistory = mockk<Tab>()
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
        val webHistory = mockk<Tab>()
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
        val bookmark = mockk<Tab>()
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
        val bookmark = mockk<Tab>()
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
    fun receivePathFlow() {
        every { mainViewModel.droppedPathFlow() } returns flowOf(
            makePath("jpg"),
            makePath("webp"),
            makePath("png"),
            makePath("txt")
        )
        every { mainViewModel.openTab(any()) } just Runs
        every { mainViewModel.currentTab() } returns mockk()

        runTest {
            subject.receivePathFlow()
        }

        verify { mainViewModel.droppedPathFlow() }
        verify(exactly = 3) { mainViewModel.openTab(any()) }
    }


    @Test
    fun receivePathFlowWhenCurrentIsFileRenameToolTab() {
        every { mainViewModel.droppedPathFlow() } returns flowOf(
            makePath("jpg"),
            makePath("webp"),
            makePath("png"),
            makePath("txt")
        )
        every { mainViewModel.openTab(any()) } just Runs
        every { mainViewModel.currentTab() } returns mockk<FileRenameToolTab>()

        runTest {
            subject.receivePathFlow()
        }

        verify { mainViewModel.droppedPathFlow() }
        verify(inverse = true) { mainViewModel.openTab(any()) }
    }

    private fun makePath(extension: String): Path {
        val path = mockk<Path>()
        every { path.fileName } returns path
        every { path.toString() } returns "test.$extension"
        return path
    }

}