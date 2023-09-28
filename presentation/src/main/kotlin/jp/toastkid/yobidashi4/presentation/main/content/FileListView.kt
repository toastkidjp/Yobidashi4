package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.awt.Desktop
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import jp.toastkid.yobidashi4.domain.service.archive.ZipArchiver
import jp.toastkid.yobidashi4.presentation.editor.legacy.service.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.main.content.data.FileListItem
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun FileListView(paths: List<Path>, modifier: Modifier = Modifier) {
    val completeItems = remember { mutableStateListOf<FileListItem>() }
    val articleStates = remember { mutableStateListOf<FileListItem>() }

    LaunchedEffect(paths.size) {
        articleStates.clear()
        val editableExtensions = setOf("md", "txt")
        paths.map { FileListItem(it, editable = editableExtensions.contains(it.extension)) }.forEach { articleStates.add(it) }
        completeItems.addAll(articleStates)
    }

    val dateTimeFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd(E) HH:mm:ss").withLocale(Locale.ENGLISH) }

    val viewModel = remember { object : KoinComponent { val vm: MainViewModel by inject() }.vm }
    val coroutineScope = rememberCoroutineScope()

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp,
        modifier = modifier
    ) {
        Box(modifier = Modifier) {
            val state = rememberLazyListState()
            val horizontalScrollState = rememberScrollState()
            var controlPressing = false
            var shiftPressing = false
            LazyColumn(
                state = state,
                userScrollEnabled = true,
                modifier = Modifier
                    .onKeyEvent { keyEvent ->
                        controlPressing = keyEvent.isCtrlPressed
                        shiftPressing = keyEvent.isShiftPressed

                        if (keyEvent.isCtrlPressed && keyEvent.key == Key.Z) {
                            ZipArchiver().invoke(articleStates.filter { it.selected }.map { it.path })
                            Desktop.getDesktop().open(File("."))
                            return@onKeyEvent true
                        }
                        if (keyEvent.key == Key.DirectionUp) {
                            coroutineScope.launch {
                                state.scrollToItem(max(0, state.firstVisibleItemIndex - 1), 0)
                            }
                            return@onKeyEvent true
                        }
                        if (keyEvent.key == Key.DirectionDown) {
                            coroutineScope.launch {
                                state.scrollToItem(min(articleStates.size - 1, state.firstVisibleItemIndex + 1), 0)
                            }
                            return@onKeyEvent true
                        }
                        false
                    }
            ) {
                stickyHeader {
                    val keyword = remember { mutableStateOf(TextFieldValue()) }
                    TextField(
                        keyword.value,
                        maxLines = 1,
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = if (state.firstVisibleItemIndex == 0) Color.Transparent
                            else MaterialTheme.colors.surface.copy(alpha = 0.75f) ,
                            cursorColor = MaterialTheme.colors.secondary
                        ),
                        label = { Text("Keyword", color = MaterialTheme.colors.secondary) },
                        onValueChange = {
                            keyword.value = TextFieldValue(it.text, it.selection, it.composition)
                            if (keyword.value.composition == null) {
                                articleStates.clear()
                                articleStates.addAll(
                                    if (keyword.value.text.isNotBlank()) completeItems.filter { item -> item.path.nameWithoutExtension.lowercase().contains(keyword.value.text.lowercase()) }
                                    else completeItems
                                )
                                return@TextField
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        trailingIcon = {
                            Icon(
                                painterResource("images/icon/ic_clear_form.xml"),
                                contentDescription = "Clear input.",
                                tint = MaterialTheme.colors.secondary,
                                modifier = Modifier.clickable {
                                    keyword.value = TextFieldValue()
                                    articleStates.clear()
                                    articleStates.addAll(completeItems)
                                }
                            )
                        }
                    )
                }

                itemsIndexed(articleStates) { index, fileListItem ->
                    FileListItemRow(
                        fileListItem,
                        index,
                        dateTimeFormatter,
                        { articleStates.filter { it.selected }.map { it.path } },
                        modifier.animateItemPlacement()
                            .combinedClickable(
                                enabled = true,
                                onClick = {
                                    val clickedIndex = articleStates.indexOf(fileListItem)

                                    if (shiftPressing) {
                                        val startIndex = articleStates.indexOfFirst { it.selected }
                                        val range =
                                            if (startIndex < clickedIndex) (startIndex + 1)..clickedIndex else (clickedIndex until startIndex)
                                        range.forEach { targetIndex ->
                                            articleStates.set(targetIndex, articleStates.get(targetIndex).reverseSelection())
                                        }
                                        return@combinedClickable
                                    }

                                    if (controlPressing.not() && shiftPressing.not()) {
                                        articleStates.mapIndexed { i, fileListItem ->
                                            i to fileListItem
                                        }
                                            .filter { it.second.selected }
                                            .forEach {
                                                articleStates.set(it.first, it.second.unselect())
                                            }
                                    }

                                    articleStates.set(clickedIndex, fileListItem.reverseSelection())
                                },
                                onLongClick = {
                                    viewModel.openFile(fileListItem.path, true)
                                },
                                onDoubleClick = {
                                    viewModel.openFile(fileListItem.path)
                                    val extension = fileListItem.path.extension
                                    if (extension == "md" || extension == "txt") {
                                        viewModel.hideArticleList()
                                    }
                                }
                            )
                    )
                }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(state),
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
            )
            HorizontalScrollbar(
                adapter = rememberScrollbarAdapter(horizontalScrollState),
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
    dateTimeFormatter: DateTimeFormatter?,
    selectedFiles: () -> List<Path>,
    modifier: Modifier
) {
    val viewModel = remember { object : KoinComponent { val vm: MainViewModel by inject() }.vm }
    val openOption = remember { mutableStateOf(false) }
    val cursorOn = remember { mutableStateOf(false) }

    val backgroundColor = animateColorAsState(if (cursorOn.value) MaterialTheme.colors.primary else Color.Transparent)

    Box(
        modifier = modifier.drawBehind { drawRect(backgroundColor.value) }
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
            .background(
                if (fileListItem.selected) MaterialTheme.colors.primary.copy(alpha = 0.5f) else if (index % 2 == 0) MaterialTheme.colors.surface.copy(
                    alpha = 0.5f
                ) else Color.Transparent
            )
            .padding(horizontal = 16.dp)
        ) {
            val textColor = if (cursorOn.value) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
            Text(
                fileListItem.path.nameWithoutExtension,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textColor
            )
            if (Files.exists(fileListItem.path)) {
                Text(
                    "${Files.size(fileListItem.path) / 1000} KB | ${
                        LocalDateTime
                            .ofInstant(Files.getLastModifiedTime(fileListItem.path).toInstant(), ZoneId.systemDefault())
                            .format(dateTimeFormatter)
                    }",
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
                    if (fileListItem.editable) "Edit" else "Open",
                    modifier = Modifier.padding(8.dp).fillMaxSize()
                )
            }

            if (fileListItem.editable) {
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
                        viewModel.openFile(fileListItem.path, true)
                        openOption.value = false
                    }
                ) {
                    Text(
                        "バックグラウンドで開く",
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
                        "内部リンクをコピー",
                        modifier = Modifier.padding(8.dp).fillMaxSize()
                    )
                }
            }
        }
    }
}