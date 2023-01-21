package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.tab.CalendarTab
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.FileTab
import jp.toastkid.yobidashi4.domain.model.tab.LoanCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.NumberPlaceGameTab
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.calendar.CalendarView
import jp.toastkid.yobidashi4.presentation.editor.EditorTabView
import jp.toastkid.yobidashi4.presentation.editor.legacy.LegacyEditorView
import jp.toastkid.yobidashi4.presentation.editor.legacy.service.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.loan.LoanCalculatorView
import jp.toastkid.yobidashi4.presentation.number.NumberPlaceView
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import jp.toastkid.yobidashi4.presentation.viewmodel.web.WebTabViewModel
import jp.toastkid.yobidashi4.presentation.web.WebTabView
import jp.toastkid.yobidashi4.presentation.web.bookmark.WebBookmarkTabView
import kotlin.io.path.name
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TabsView(modifier: Modifier) {
    val viewModel = remember {
        MainViewModel.get()
    }

    if (viewModel.tabs.isEmpty()) {
        return
    }

    Column(modifier = modifier) {
        ScrollableTabRow(selectedTabIndex = viewModel.selected.value) {
            viewModel.tabs.forEachIndexed { index, tab ->
                val openDropdownMenu = remember { mutableStateOf(false) }
                Tab(
                    selected = viewModel.selected.value == index,
                    onClick = { viewModel.setSelectedIndex(index) },
                    modifier = Modifier
                        .pointerInput(Unit) {
                            forEachGesture {
                                awaitPointerEventScope {
                                    val awaitPointerEvent = awaitPointerEvent()
                                    if (awaitPointerEvent.type == PointerEventType.Press
                                        && !openDropdownMenu.value
                                        && awaitPointerEvent.button == PointerButton.Secondary) {
                                        openDropdownMenu.value = true
                                    }
                                }
                            }
                        }
                ) {
                    Box {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            tab.iconPath()?.let {
                                Icon(
                                    painterResource(it),
                                    contentDescription = "Tab's icon",
                                    tint = if (tab.useIconTint()) MaterialTheme.colors.onPrimary else Color.Transparent
                                )
                            }

                            Text(tab.title(),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                modifier = Modifier.widthIn(max = if (tab is WebTab) 180.dp else 1000.dp).padding(vertical = 8.dp).padding(start = 8.dp))
                            if (tab.closeable()) {
                                Text("x",
                                    modifier = Modifier
                                        .padding(start = 4.dp)
                                        .background(MaterialTheme.colors.surface.copy(alpha = 0.2f))
                                        .clickable { viewModel.removeTabAt(index) }
                                        .padding(8.dp)
                                )
                            }
                        }
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
                                Text("タイトルコピー")
                            }
                            if (tab is WebTab) {
                                DropdownMenuItem(
                                    onClick = {
                                        ClipboardPutterService().invoke(tab.url())
                                        openDropdownMenu.value = false
                                    }
                                ) {
                                    Text("URLコピー")
                                }
                                DropdownMenuItem(
                                    onClick = {
                                        if (viewModel.tabs[viewModel.selected.value] is WebTab) {
                                            object : KoinComponent { val vm: WebTabViewModel by inject() }
                                                .vm.reload(tab.id())
                                        }
                                        openDropdownMenu.value = false
                                    }
                                ) {
                                    Text("リロード")
                                }
                            }
                        }
                    }
                }
            }
        }

        when (val currentTab = viewModel.tabs[viewModel.selected.value]) {
            is FileTab -> FileList(currentTab.items)
            is TableTab -> TableView(currentTab.items)
            is EditorTab -> {
                if (currentTab.path.name.contains("Sandbox")) {
                    EditorTabView(currentTab)
                } else {
                    LegacyEditorView(currentTab)
                }
            }
            is CalendarTab -> CalendarView()
            is WebTab -> WebTabView(currentTab)
            is WebBookmarkTab -> WebBookmarkTabView()
            is NumberPlaceGameTab -> NumberPlaceView()
            is LoanCalculatorTab -> LoanCalculatorView()
            else -> Unit
        }
    }
}