package jp.toastkid.yobidashi4.presentation.web.bookmark

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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.presentation.component.LoadIcon
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.absolutePathString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun WebBookmarkTabView() {
    val viewModel = remember { WebBookmarkTabViewModel() }
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
                items(viewModel.bookmarks()) { bookmark ->
                    WebBookmarkItemRow(
                        bookmark,
                        {
                            viewModel.delete(bookmark)
                        },
                        Modifier.animateItemPlacement()
                            .combinedClickable(
                                enabled = true,
                                onClick = {
                                    viewModel.openUrl(bookmark.url, false)
                                },
                                onLongClick = {
                                    viewModel.openUrl(bookmark.url, true)
                                }
                            )
                    )
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(viewModel.listState()),
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
            )

            LaunchedEffect(Unit) {
                viewModel.launch()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun WebBookmarkItemRow(
    bookmark: Bookmark,
    onDelete: () -> Unit,
    modifier: Modifier
) {
    val cursorOn = remember { mutableStateOf(false) }
    val backgroundColor = animateColorAsState(if (cursorOn.value) MaterialTheme.colors.primary else Color.Transparent)
    val openOption = remember { mutableStateOf(false) }

    val mainViewModel = remember { object : KoinComponent { val vm: MainViewModel by inject() }.vm }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .drawBehind { drawRect(backgroundColor.value) }
                .onPointerEvent(PointerEventType.Enter) {
                    cursorOn.value = true
                }
                .onPointerEvent(PointerEventType.Exit) {
                    cursorOn.value = false
                }
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val awaitPointerEvent = awaitPointerEvent()
                        if (awaitPointerEvent.type == PointerEventType.Press
                            && !openOption.value
                            && awaitPointerEvent.button == PointerButton.Secondary
                        ) {
                            openOption.value = true
                        }
                    }
                }
        ) {
            val faviconFolder = WebIcon()
            faviconFolder.makeFolderIfNeed()
            val iconPath = faviconFolder.find(bookmark.url)
            LoadIcon(iconPath?.absolutePathString(), Modifier.size(32.dp).padding(start = 4.dp).padding(horizontal = 4.dp))
            Column(modifier = Modifier
                .padding(horizontal = 16.dp)
            ) {
                val textColor = if (cursorOn.value) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
                Text(bookmark.title, color = textColor)
                Text(bookmark.url, maxLines = 1, overflow = TextOverflow.Ellipsis, color = textColor)
                Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))
            }
        }

        DropdownMenu(
            expanded = openOption.value,
            onDismissRequest = {
                openOption.value = false
            }
        ) {
            DropdownMenuItem(
                onClick = {
                    mainViewModel.openUrl(bookmark.url, false)
                    openOption.value = false
                }
            ) {
                Text(
                    "Open",
                    modifier = Modifier.padding(8.dp).fillMaxSize()
                )
            }

            DropdownMenuItem(
                onClick = {
                    mainViewModel.openUrl(bookmark.url, true)
                    openOption.value = false
                }
            ) {
                Text(
                    "Open background",
                    modifier = Modifier.padding(8.dp).fillMaxSize()
                )
            }
            DropdownMenuItem(
                onClick = {
                    mainViewModel.browseUri(bookmark.url)
                    openOption.value = false
                }
            ) {
                Text(
                    "Open with browser",
                    modifier = Modifier.padding(8.dp).fillMaxSize()
                )
            }
            DropdownMenuItem(
                onClick = {
                    ClipboardPutterService().invoke(bookmark.title)
                    openOption.value = false
                }
            ) {
                Text(
                    "Copy title",
                    modifier = Modifier.padding(8.dp).fillMaxSize()
                )
            }
            DropdownMenuItem(
                onClick = {
                    ClipboardPutterService().invoke(bookmark.url)
                    openOption.value = false
                }
            ) {
                Text(
                    "Copy URL",
                    modifier = Modifier.padding(8.dp).fillMaxSize()
                )
            }
            DropdownMenuItem(
                onClick = {
                    ClipboardPutterService().invoke("[${bookmark.title}](${bookmark.url})")
                    openOption.value = false
                }
            ) {
                Text(
                    "Clip markdown link",
                    modifier = Modifier.padding(8.dp).fillMaxSize()
                )
            }
            DropdownMenuItem(
                onClick = {
                    onDelete()
                    openOption.value = false
                }
            ) {
                Text(
                    "Delete",
                    modifier = Modifier.padding(8.dp).fillMaxSize()
                )
            }
        }
    }
}