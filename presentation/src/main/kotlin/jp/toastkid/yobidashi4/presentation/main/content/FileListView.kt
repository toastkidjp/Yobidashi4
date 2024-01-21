package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.nio.file.Path
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.main.content.data.FileListItem
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.nameWithoutExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FileListView(paths: List<Path>, modifier: Modifier = Modifier) {
    val viewModel = remember { FileListViewModel() }

    LaunchedEffect(paths.size) {
        viewModel.start(paths)
    }

    val coroutineScope = rememberCoroutineScope()

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp,
        modifier = modifier
    ) {
        Box(modifier = Modifier) {
            LazyColumn(
                state = viewModel.listState(),
                userScrollEnabled = true,
                modifier = Modifier
                    .onKeyEvent { keyEvent ->
                        viewModel.onKeyEvent(coroutineScope, keyEvent)
                    }
            ) {
                stickyHeader {
                    TextField(
                        viewModel.keyword(),
                        maxLines = 1,
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = if (viewModel.currentIsTop()) Color.Transparent
                            else MaterialTheme.colors.surface.copy(alpha = 0.75f) ,
                            cursorColor = MaterialTheme.colors.secondary
                        ),
                        label = { Text("Keyword", color = MaterialTheme.colors.secondary) },
                        onValueChange = {
                            viewModel.onValueChange(it)
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        trailingIcon = {
                            Icon(
                                painterResource("images/icon/ic_clear_form.xml"),
                                contentDescription = "Clear input.",
                                tint = MaterialTheme.colors.secondary,
                                modifier = Modifier.clickable {
                                    viewModel.clearInput()
                                }
                            )
                        }
                    )
                }

                itemsIndexed(viewModel.items()) { index, fileListItem ->
                    FileListItemRow(
                        fileListItem,
                        index,
                        { viewModel.items().filter { it.selected }.map { it.path } },
                        modifier.animateItemPlacement()
                            .combinedClickable(
                                enabled = true,
                                onClick = {
                                    viewModel.onSingleClick(fileListItem)
                                },
                                onLongClick = {
                                    viewModel.onLongClick(fileListItem)
                                },
                                onDoubleClick = {
                                    viewModel.onDoubleClick(fileListItem)
                                }
                            )
                    )
                }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(viewModel.listState()),
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
            )
            HorizontalScrollbar(
                adapter = rememberScrollbarAdapter(viewModel.horizontalScrollState()),
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
private fun FileListItemRow(
    fileListItem: FileListItem,
    index: Int,
    selectedFiles: () -> List<Path>,
    modifier: Modifier
) {
    val viewModel = remember { object : KoinComponent { val vm: MainViewModel by inject() }.vm }
    val openOption = remember { mutableStateOf(false) }
    val cursorOn = remember { mutableStateOf(false) }

    val underlay = if (fileListItem.selected) MaterialTheme.colors.primary.copy(alpha = 0.5f)
    else if (index % 2 == 0) MaterialTheme.colors.surface.copy(alpha = 0.5f)
    else Color.Transparent
    val backgroundColor = animateColorAsState(if (cursorOn.value) MaterialTheme.colors.primary else Color.Transparent)

    Box(
        modifier = modifier.drawBehind { drawRect(underlay) }
            .onPointerEvent(PointerEventType.Enter) {
                cursorOn.value = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                cursorOn.value = false
            }
    ) {
        Column(modifier = Modifier
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
            .drawBehind { drawRect(backgroundColor.value) }
            .padding(horizontal = 16.dp)
        ) {
            val textColor = if (cursorOn.value) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
            Text(
                fileListItem.path.nameWithoutExtension,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textColor
            )
            fileListItem.subText()?.let {
                Text(
                    it,
                    color = textColor
                )
            }
            Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))
        }

        DropdownMenu(
            openOption.value,
            onDismissRequest = { openOption.value = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    selectedFiles().ifEmpty { listOf(fileListItem.path) }.forEach {
                        viewModel.openFile(it)
                    }
                    openOption.value = false
                }
            ) {
                Text(
                    "Open",
                    modifier = Modifier.padding(8.dp).fillMaxSize()
                )
            }

            if (fileListItem.editable) {
                DropdownMenuItem(
                    onClick = {
                        selectedFiles().ifEmpty { listOf(fileListItem.path) }.forEach {
                            viewModel.edit(it)
                        }
                        openOption.value = false
                    }
                ) {
                    Text(
                        "Edit",
                        modifier = Modifier.padding(8.dp).fillMaxSize()
                    )
                }
                DropdownMenuItem(
                    onClick = {
                        selectedFiles().ifEmpty { listOf(fileListItem.path) }.forEach {
                            viewModel.openPreview(it)
                        }
                        openOption.value = false
                    }
                ) {
                    Text(
                        "Preview",
                        modifier = Modifier.padding(8.dp).fillMaxSize()
                    )
                }
                DropdownMenuItem(
                    onClick = {
                        viewModel.openFile(fileListItem.path)
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
                        viewModel.slideshow(fileListItem.path)
                        openOption.value = false
                    }
                ) {
                    Text(
                        "Slideshow",
                        modifier = Modifier.padding(8.dp).fillMaxSize()
                    )
                }
                DropdownMenuItem(
                    onClick = {
                        ClipboardPutterService().invoke(fileListItem.path.nameWithoutExtension)
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
                        ClipboardPutterService().invoke("[[${fileListItem.path.nameWithoutExtension}]]")
                        openOption.value = false
                    }
                ) {
                    Text(
                        "Clip internal link",
                        modifier = Modifier.padding(8.dp).fillMaxSize()
                    )
                }
            }
        }
    }
}