package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.tab.BarcodeToolTab
import jp.toastkid.yobidashi4.domain.model.tab.CalendarTab
import jp.toastkid.yobidashi4.domain.model.tab.CompoundInterestCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.ConverterToolTab
import jp.toastkid.yobidashi4.domain.model.tab.EditorSettingTab
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.FileRenameToolTab
import jp.toastkid.yobidashi4.domain.model.tab.FileTab
import jp.toastkid.yobidashi4.domain.model.tab.LoanCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.domain.model.tab.NotificationListTab
import jp.toastkid.yobidashi4.domain.model.tab.NumberPlaceGameTab
import jp.toastkid.yobidashi4.domain.model.tab.Reloadable
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.domain.model.tab.TextFileViewerTab
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.tab.WebHistoryTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.service.table.TableContentExporter
import jp.toastkid.yobidashi4.presentation.barcode.BarcodeToolTabView
import jp.toastkid.yobidashi4.presentation.calendar.CalendarView
import jp.toastkid.yobidashi4.presentation.component.LoadIcon
import jp.toastkid.yobidashi4.presentation.compound.CompoundInterestCalculatorView
import jp.toastkid.yobidashi4.presentation.converter.ConverterToolTabView
import jp.toastkid.yobidashi4.presentation.editor.EditorTabView
import jp.toastkid.yobidashi4.presentation.editor.setting.EditorSettingComponent
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.loan.LoanCalculatorView
import jp.toastkid.yobidashi4.presentation.log.viewer.TextFileViewerTabView
import jp.toastkid.yobidashi4.presentation.markdown.MarkdownTabView
import jp.toastkid.yobidashi4.presentation.number.NumberPlaceView
import jp.toastkid.yobidashi4.presentation.tool.file.FileRenameToolView
import jp.toastkid.yobidashi4.presentation.tool.notification.NotificationListTabView
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import jp.toastkid.yobidashi4.presentation.web.WebTabView
import jp.toastkid.yobidashi4.presentation.web.bookmark.WebBookmarkTabView
import jp.toastkid.yobidashi4.presentation.web.history.WebHistoryView
import kotlin.io.path.nameWithoutExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun TabsView(modifier: Modifier) {
    val viewModel = remember { object : KoinComponent { val vm: MainViewModel by inject() }.vm }

    if (viewModel.tabs.isEmpty()) {
        return
    }

    Column(modifier = modifier) {
        ScrollableTabRow(
            backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.75f),
            selectedTabIndex = viewModel.selected.value,
            indicator = { tabPositions ->
                val currentTabIndex = if (viewModel.selected.value == tabPositions.size) 0 else viewModel.selected.value
                if (currentTabIndex >= tabPositions.size) {
                    return@ScrollableTabRow
                }

                Divider(modifier = Modifier
                    .tabIndicatorOffset(tabPositions[currentTabIndex])
                    .height(2.dp)
                    .clip(RoundedCornerShape(8.dp)) // clip modifier not working
                    .padding(horizontal = 4.dp)
                    .background(color = MaterialTheme.colors.onPrimary)
                )
            }
        ) {
            viewModel.tabs.forEachIndexed { index, tab ->
                val openDropdownMenu = remember { mutableStateOf(false) }
                val titleState = remember { mutableStateOf(tab.title()) }
                val iconPathState = remember { mutableStateOf(tab.iconPath()) }
                LaunchedEffect("${index}_${tab.hashCode()}") {
                    titleState.value = tab.title()
                    iconPathState.value = tab.iconPath()

                    tab.update().collect {
                        titleState.value = tab.title()
                        iconPathState.value = tab.iconPath()
                    }
                }

                Tab(
                    selected = viewModel.selected.value == index,
                    onClick = { viewModel.setSelectedIndex(index) },
                    modifier = Modifier
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                val awaitPointerEvent = awaitPointerEvent()
                                if (awaitPointerEvent.type == PointerEventType.Press
                                    && !openDropdownMenu.value
                                    && awaitPointerEvent.button == PointerButton.Secondary) {
                                    openDropdownMenu.value = true
                                }
                            }
                        }
                ) {
                    Box {
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            LoadIcon(iconPathState.value, Modifier.size(24.dp).padding(start = 4.dp))

                            val width = if (tab is WebTab) 232.dp else 1000.dp
                            Text(titleState.value,
                                color = MaterialTheme.colors.onPrimary,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                modifier = Modifier.widthIn(max = width).padding(vertical = 8.dp).padding(start = 8.dp))
                            if (tab.closeable()) {
                                Text("x",
                                    color = MaterialTheme.colors.onPrimary,
                                    modifier = Modifier
                                        .padding(start = 4.dp)
                                        .background(MaterialTheme.colors.surface.copy(alpha = 0.2f))
                                        .clickable { viewModel.removeTabAt(index) }
                                        .padding(8.dp)
                                )
                            }
                        }

                        TabOptionMenu(openDropdownMenu, tab, viewModel)
                    }
                }
            }
        }

        when (val currentTab = viewModel.currentTab()) {
            is FileTab -> FileListView(currentTab.items, Modifier)
            is TableTab -> TableView(currentTab.items())
            is EditorTab -> EditorTabView(currentTab)
            is MarkdownPreviewTab -> MarkdownTabView(currentTab, Modifier)
            is EditorSettingTab -> EditorSettingComponent(modifier = Modifier)
            is CalendarTab -> CalendarView()
            is CompoundInterestCalculatorTab -> CompoundInterestCalculatorView()
            is FileRenameToolTab -> FileRenameToolView()
            is WebTab -> WebTabView(currentTab)
            is WebBookmarkTab -> WebBookmarkTabView()
            is WebHistoryTab -> WebHistoryView()
            is NumberPlaceGameTab -> NumberPlaceView()
            is LoanCalculatorTab -> LoanCalculatorView()
            is TextFileViewerTab -> TextFileViewerTabView(currentTab)
            is ConverterToolTab -> ConverterToolTabView()
            is BarcodeToolTab -> BarcodeToolTabView()
            is NotificationListTab -> NotificationListTabView()
            else -> Unit
        }
    }
}

@Composable
private fun TabOptionMenu(
    openDropdownMenu: MutableState<Boolean>,
    tab: Tab,
    viewModel: MainViewModel
) {
    DropdownMenu(
        expanded = openDropdownMenu.value,
        onDismissRequest = {
            openDropdownMenu.value = false
        }
    ) {
        DropdownMenuItem(
            onClick = {
                ClipboardPutterService().invoke(tab.title())
                openDropdownMenu.value = false
            }
        ) {
            Text("Copy title")
        }

        DropdownMenuItem(
            onClick = {
                viewModel.closeOtherTabs()
                openDropdownMenu.value = false
            }
        ) {
            Text("Close other tabs")
        }

        if (tab is WebTab) {
            DropdownMenuItem(
                onClick = {
                    ClipboardPutterService().invoke(tab.url())
                    openDropdownMenu.value = false
                }
            ) {
                Text("Copy URL")
            }
        }

        if (tab is Reloadable) {
            DropdownMenuItem(
                onClick = {
                    tab.reload()
                    openDropdownMenu.value = false
                }
            ) {
                Text("Reload")
            }
        }

        if (tab is MarkdownPreviewTab) {
            DropdownMenuItem(onClick = {
                openDropdownMenu.value = false
                viewModel.edit(tab.slideshowSourcePath())
            }) {
                Text("Edit")
            }
        }

        if (tab is TableTab) {
            DropdownMenuItem(onClick = {
                openDropdownMenu.value = false
                TableContentExporter().invoke(tab.items())
                viewModel.showSnackbar("Done export.", "Open") {
                    viewModel.openFile(Path.of(TableContentExporter.exportTo()), false)
                }
            }) {
                Text("Export table")
            }
        }

        if (tab is EditorTab) {
            DropdownMenuItem(
                onClick = {
                    openDropdownMenu.value = false
                    ClipboardPutterService().invoke("[[${tab.path.nameWithoutExtension}]]")
                }
            ) {
                Text("Clip internal link")
            }
        }
    }
}
