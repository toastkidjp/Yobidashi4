package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
import jp.toastkid.yobidashi4.domain.model.tab.FileRenameToolTab
import jp.toastkid.yobidashi4.domain.model.tab.PhotoTab
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.repository.chat.ChatExporter
import jp.toastkid.yobidashi4.domain.service.table.TableContentExporter
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.extension
import kotlinx.coroutines.flow.filter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TabsViewModel  : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val chatExporter: ChatExporter by inject()

    fun tabIsEmpty() = viewModel.tabs.isEmpty()

    fun selectedTabIndex() = viewModel.selected.value

    fun currentTabIndex(tabPositionsSize: Int) = if (viewModel.selected.value == tabPositionsSize) 0 else viewModel.selected.value

    fun tabs(): List<Tab> = viewModel.tabs

    fun isSelectedIndex(index: Int): Boolean {
        return viewModel.selected.value == index
    }

    fun setSelectedIndex(index: Int) {
        viewModel.setSelectedIndex(index)
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun onPointerEvent(awaitPointerEvent: PointerEvent, tab: Tab) {
        if (awaitPointerEvent.type == PointerEventType.Press
            && !openingDropdown(tab)
            && awaitPointerEvent.button == PointerButton.Secondary) {
            openDropdown(tab)
        }
    }

    fun currentTab(): Tab? {
        return viewModel.currentTab()
    }

    fun removeTabAt(index: Int) {
        viewModel.removeTabAt(index)
    }

    fun exportTable(tableItems: AggregationResult) {
        TableContentExporter().invoke(tableItems)
        viewModel.showSnackbar("Done export.", "Open") {
            viewModel.openFile(Path.of(TableContentExporter.exportTo()))
        }
    }

    fun exportChat(tab: ChatTab) {
        chatExporter.invoke(tab.chat())
        closeDropdown()
    }

    fun closeOtherTabs() {
        viewModel.closeOtherTabs()
    }

    fun edit(path: Path) {
        viewModel.edit(path)
    }

    private val currentDropdownItem = mutableStateOf<Tab?>(null)

    fun openingDropdown(tab: Tab): Boolean = currentDropdownItem.value == tab

    fun openDropdown(tab: Tab) {
        currentDropdownItem.value = tab
    }

    fun closeDropdown() {
        currentDropdownItem.value = null
    }

    fun calculateTabWidth(tab: Tab): Dp {
        return if (tab is WebTab) 232.dp else 1000.dp
    }

    suspend fun receivePathFlow() {
        viewModel.droppedPathFlow()
            .filter { viewModel.currentTab() !is FileRenameToolTab && setOf("jpg", "webp", "png", "gif").contains(it.extension) }
            .collect { viewModel.openTab(PhotoTab(it)) }
    }

}