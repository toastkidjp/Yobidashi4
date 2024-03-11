package jp.toastkid.yobidashi4.presentation.web.history

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.tab.WebHistoryTab
import jp.toastkid.yobidashi4.presentation.component.LoadIcon
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun WebHistoryView(tab: WebHistoryTab) {
    val viewModel = remember { WebHistoryViewModel() }
    val coroutineScope = rememberCoroutineScope()

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp,
        modifier = Modifier.onKeyEvent {
            return@onKeyEvent viewModel.scrollAction(coroutineScope, it.key, it.isCtrlPressed)
        }.focusRequester(viewModel.focusRequester()).focusable(true)
    ) {
        Box {
            LazyColumn(
                state = viewModel.listState(),
                userScrollEnabled = true,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                items(viewModel.list()) { webHistory ->
                    val cursorOn = remember { mutableStateOf(false) }
                    val backgroundColor = animateColorAsState(if (cursorOn.value) MaterialTheme.colors.primary else Color.Transparent)

                    Box(
                        Modifier.pointerInput(Unit) {
                            awaitEachGesture {
                                viewModel.onPointerEvent(awaitPointerEvent(), webHistory)
                            }
                        }
                            .animateItemPlacement()
                            .drawBehind { drawRect(backgroundColor.value) }
                            .onPointerEvent(PointerEventType.Enter) {
                                cursorOn.value = true
                            }
                            .onPointerEvent(PointerEventType.Exit) {
                                cursorOn.value = false
                            }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LoadIcon(viewModel.findIconPath(webHistory), Modifier.size(32.dp).padding(start = 4.dp).padding(horizontal = 4.dp))
                            Column(modifier = Modifier
                                .combinedClickable(
                                    enabled = true,
                                    onClick = {
                                        viewModel.openUrl(webHistory.url, false)
                                    },
                                    onLongClick = {
                                        viewModel.openUrl(webHistory.url, true)
                                    }
                                )
                                .padding(horizontal = 16.dp)
                            ) {
                                val textColor = if (cursorOn.value) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface

                                Text(webHistory.title, color = textColor)
                                Text(webHistory.url, maxLines = 1, overflow = TextOverflow.Ellipsis, color = textColor)
                                Text(
                                    viewModel.dateTimeString(webHistory),
                                    color = textColor,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))
                            }
                        }

                        DropdownMenu(
                            expanded = viewModel.openingDropdown(webHistory),
                            onDismissRequest = viewModel::closeDropdown
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    viewModel.openUrl(webHistory.url, false)
                                    viewModel.closeDropdown()
                                }
                            ) {
                                Text(
                                    "Open",
                                    modifier = Modifier.padding(8.dp).fillMaxSize()
                                )
                            }

                            DropdownMenuItem(
                                onClick = {
                                    viewModel.openUrl(webHistory.url, true)
                                    viewModel.closeDropdown()
                                }
                            ) {
                                Text(
                                    "Open background",
                                    modifier = Modifier.padding(8.dp).fillMaxSize()
                                )
                            }
                            DropdownMenuItem(
                                onClick = {
                                    viewModel.browseUri(webHistory.url)
                                }
                            ) {
                                Text(
                                    "Open with browser",
                                    modifier = Modifier.padding(8.dp).fillMaxSize()
                                )
                            }
                            DropdownMenuItem(
                                onClick = {
                                    ClipboardPutterService().invoke(webHistory.title)
                                    viewModel.closeDropdown()
                                }
                            ) {
                                Text(
                                    "Copy title",
                                    modifier = Modifier.padding(8.dp).fillMaxSize()
                                )
                            }
                            DropdownMenuItem(
                                onClick = {
                                    ClipboardPutterService().invoke(webHistory.url)
                                    viewModel.closeDropdown()
                                }
                            ) {
                                Text(
                                    "Copy URL",
                                    modifier = Modifier.padding(8.dp).fillMaxSize()
                                )
                            }
                            DropdownMenuItem(
                                onClick = {
                                    ClipboardPutterService().invoke("[${webHistory.title}](${webHistory.url})")
                                    viewModel.closeDropdown()
                                }
                            ) {
                                Text(
                                    "Clip markdown link",
                                    modifier = Modifier.padding(8.dp).fillMaxSize()
                                )
                            }
                            DropdownMenuItem(
                                onClick = {
                                    viewModel.delete(webHistory)
                                }
                            ) {
                                Text(
                                    "Delete",
                                    modifier = Modifier.padding(8.dp).fillMaxSize()
                                )
                            }
                            DropdownMenuItem(
                                onClick = {
                                    viewModel.clear()
                                }
                            ) {
                                Text(
                                    "Clear",
                                    modifier = Modifier.padding(8.dp).fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(viewModel.listState()),
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
            )

            DisposableEffect(tab) {
                viewModel.launch(coroutineScope, tab)

                onDispose {
                    viewModel.onDispose(tab)
                }
            }
        }
    }
}