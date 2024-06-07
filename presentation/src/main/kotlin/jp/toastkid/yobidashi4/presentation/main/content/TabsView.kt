package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.tab.BarcodeToolTab
import jp.toastkid.yobidashi4.domain.model.tab.CalendarTab
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
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
import jp.toastkid.yobidashi4.domain.model.tab.PhotoTab
import jp.toastkid.yobidashi4.domain.model.tab.Reloadable
import jp.toastkid.yobidashi4.domain.model.tab.RouletteToolTab
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.domain.model.tab.TextFileViewerTab
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.tab.WebHistoryTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.calendar.CalendarView
import jp.toastkid.yobidashi4.presentation.chat.ChatTabView
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
import jp.toastkid.yobidashi4.presentation.photo.PhotoTabView
import jp.toastkid.yobidashi4.presentation.tool.file.FileRenameToolView
import jp.toastkid.yobidashi4.presentation.tool.notification.NotificationListTabView
import jp.toastkid.yobidashi4.presentation.tool.roulette.RouletteToolTabView
import jp.toastkid.yobidashi4.presentation.web.WebTabView
import jp.toastkid.yobidashi4.presentation.web.bookmark.WebBookmarkTabView
import jp.toastkid.yobidashi4.presentation.web.history.WebHistoryView
import kotlin.io.path.nameWithoutExtension

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun TabsView(modifier: Modifier) {
    val viewModel = remember { TabsViewModel() }

    Column(modifier = modifier) {
        ScrollableTabRow(
            backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.75f),
            selectedTabIndex = viewModel.selectedTabIndex(),
            indicator = { tabPositions ->
                val currentTabIndex = viewModel.currentTabIndex(tabPositions.size)

                val currentTabPosition = tabPositions.getOrNull(currentTabIndex) ?: return@ScrollableTabRow

                Divider(modifier = Modifier
                    .tabIndicatorOffset(currentTabPosition)
                    .height(2.dp)
                    .clip(RoundedCornerShape(8.dp)) // clip modifier not working
                    .padding(horizontal = 4.dp)
                    .background(color = MaterialTheme.colors.onPrimary)
                )
            }
        ) {
            viewModel.tabs().forEachIndexed { index, tab ->
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
                    selected = viewModel.isSelectedIndex(index),
                    onClick = { viewModel.setSelectedIndex(index) },
                    modifier = Modifier
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                viewModel.onPointerEvent(awaitPointerEvent(), tab)
                            }
                        }
                ) {
                    Box {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LoadIcon(iconPathState.value, Modifier.size(24.dp).padding(start = 4.dp))

                            Text(titleState.value,
                                color = MaterialTheme.colors.onPrimary,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                modifier = Modifier.widthIn(max = viewModel.calculateTabWidth(tab)).padding(vertical = 8.dp).padding(start = 8.dp))
                            if (tab.closeable()) {
                                Text("x",
                                    color = MaterialTheme.colors.onPrimary,
                                    modifier = Modifier
                                        .padding(start = 4.dp)
                                        .background(MaterialTheme.colors.surface.copy(alpha = 0.2f))
                                        .clickable { viewModel.removeTabAt(index) }
                                        .padding(8.dp)
                                        .semantics { contentDescription = "Close button $index" }
                                )
                            }
                        }

                        TabOptionMenu(
                            viewModel.openingDropdown(tab),
                            tab,
                            viewModel::closeOtherTabs,
                            {
                                ClipboardPutterService().invoke(it)
                            },
                            {
                                viewModel.edit(it.slideshowSourcePath())
                            },
                            {
                                viewModel.exportTable(it.items())
                            },
                            viewModel::exportChat,
                            viewModel::closeDropdown
                        )
                    }
                }
            }
        }

        when (val currentTab = viewModel.currentTab()) {
            is FileTab -> FileListView(currentTab.items, Modifier)
            is TableTab -> TableView(currentTab)
            is EditorTab -> EditorTabView(currentTab)
            is MarkdownPreviewTab -> MarkdownTabView(currentTab, Modifier)
            is EditorSettingTab -> EditorSettingComponent(modifier = Modifier)
            is CalendarTab -> CalendarView(currentTab)
            is CompoundInterestCalculatorTab -> CompoundInterestCalculatorView()
            is FileRenameToolTab -> FileRenameToolView()
            is RouletteToolTab -> RouletteToolTabView()
            is WebTab -> WebTabView(currentTab)
            is WebBookmarkTab -> WebBookmarkTabView(currentTab)
            is WebHistoryTab -> WebHistoryView(currentTab)
            is NumberPlaceGameTab -> NumberPlaceView()
            is LoanCalculatorTab -> LoanCalculatorView()
            is TextFileViewerTab -> TextFileViewerTabView(currentTab)
            is ConverterToolTab -> ConverterToolTabView()
            is BarcodeToolTab -> BarcodeToolTab()
            is NotificationListTab -> NotificationListTabView()
            is ChatTab -> ChatTabView(currentTab)
            is PhotoTab -> PhotoTabView(currentTab)
            else -> Unit
        }
    }

    LaunchedEffect(Unit) {
        viewModel.receivePathFlow()
    }
}

@Composable
private fun TabOptionMenu(
    openingDropdownMenu: Boolean,
    tab: Tab,
    closeOtherTabs: () -> Unit,
    clipText: (String) -> Unit,
    edit: (MarkdownPreviewTab) -> Unit,
    exportTable: (TableTab) -> Unit,
    exportChat: (ChatTab) -> Unit,
    close: () -> Unit
) {
    DropdownMenu(
        expanded = openingDropdownMenu,
        onDismissRequest = close
    ) {
        DropdownMenuItem(
            onClick = {
                clipText(tab.title())
                close()
            }
        ) {
            Text("Copy title")
        }

        DropdownMenuItem(
            onClick = {
                closeOtherTabs()
                close()
            }
        ) {
            Text("Close other tabs")
        }

        if (tab is WebTab) {
            DropdownMenuItem(
                onClick = {
                    clipText(tab.url())
                    close()
                }
            ) {
                Text("Copy URL")
            }
        }

        if (tab is Reloadable) {
            DropdownMenuItem(
                onClick = {
                    tab.reload()
                    close()
                }
            ) {
                Text("Reload")
            }
        }

        if (tab is MarkdownPreviewTab) {
            DropdownMenuItem(onClick = {
                edit(tab)
                close()
            }) {
                Text("Edit")
            }
        }

        if (tab is TableTab) {
            DropdownMenuItem(onClick = {
                exportTable(tab)
                close()
            }) {
                Text("Export table")
            }
        }

        if (tab is EditorTab) {
            DropdownMenuItem(
                onClick = {
                    clipText("[[${tab.path.nameWithoutExtension}]]")
                    close()
                }
            ) {
                Text("Clip internal link")
            }
        }

        if (tab is ChatTab) {
            DropdownMenuItem(
                onClick = {
                    exportChat(tab)
                }
            ) {
                Text("Export chat")
            }
        }
    }
}
