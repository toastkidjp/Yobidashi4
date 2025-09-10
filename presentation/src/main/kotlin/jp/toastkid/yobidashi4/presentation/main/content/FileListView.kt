package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_reload
import jp.toastkid.yobidashi4.presentation.component.HoverHighlightColumn
import jp.toastkid.yobidashi4.presentation.component.HoverHighlightDropdownMenuItem
import jp.toastkid.yobidashi4.presentation.component.SingleLineTextField
import jp.toastkid.yobidashi4.presentation.main.content.data.FileListItem
import org.jetbrains.compose.resources.painterResource
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun FileListView(paths: List<Path>, modifier: Modifier = Modifier) {
    val viewModel = remember { FileListViewModel() }

    LaunchedEffect(paths.size) {
        viewModel.start(paths)
    }

    val coroutineScope = rememberCoroutineScope()

    val oddBackground = MaterialTheme.colors.primary.copy(alpha = 0.5f)
    val evenBackground = MaterialTheme.colors.surface.copy(alpha = 0.5f)

    val stickyHeaderBackgroundColor = animateColorAsState(
        if (viewModel.listState().firstVisibleItemIndex != 0) MaterialTheme.colors.surface
        else Color.Transparent
    )

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
                    .semantics { contentDescription = "File list" }
            ) {
                stickyHeader {
                    Row(
                        modifier = Modifier
                            .drawBehind {
                                drawRect(stickyHeaderBackgroundColor.value)
                            }
                    ) {
                        SingleLineTextField(
                            viewModel.keyword(),
                            "Keyword",
                            viewModel::onValueChange,
                            viewModel::clearInput,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            painterResource(Res.drawable.ic_reload),
                            contentDescription = "Reload file list",
                            tint = MaterialTheme.colors.secondary,
                            modifier = Modifier.clickable(onClick = {
                                viewModel.start(paths)
                            })
                                .padding(8.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }

                itemsIndexed(viewModel.items(), key = { i, fileListItem -> "${i}_" + fileListItem.path}) { index, fileListItem ->
                    val underlay = derivedStateOf {
                        if (fileListItem.selected) oddBackground
                        else if (index % 2 == 0) evenBackground
                        else Color.Transparent
                    }

                    FileListItemRow(
                        fileListItem,
                        viewModel.openingDropdown(fileListItem),
                        viewModel::closeDropdown,
                        viewModel::selectedFiles,
                        viewModel::openFile,
                        { viewModel.edit(fileListItem.path) },
                        { viewModel.preview(fileListItem.path) },
                        { viewModel.slideshow(fileListItem.path) },
                        viewModel::clipText,
                        modifier
                            .animateItem(fadeInSpec = null)
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
                            .drawBehind { drawRect(underlay.value) }
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    viewModel.onPointerEvent(awaitPointerEvent(), index)
                                }
                            }
                            .onKeyEvent {
                                viewModel.onKeyEventFromCell(it, fileListItem)
                            }
                            .semantics { contentDescription = fileListItem.path.nameWithoutExtension }
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
private fun FileListItemRow(
    fileListItem: FileListItem,
    openOption: Boolean,
    closeOption: () -> Unit,
    selectedFiles: () -> List<Path>,
    openFile: (Path) -> Unit,
    edit: () -> Unit,
    preview: () -> Unit,
    slideshow: () -> Unit,
    clipText: (String) -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = modifier
    ) {
        HoverHighlightColumn(modifier = Modifier
            .padding(horizontal = 16.dp)
        ) { textColor ->
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
            openOption,
            onDismissRequest = closeOption
        ) {
            HoverHighlightDropdownMenuItem(
                "Open"
            ) {
                selectedFiles().ifEmpty { listOf(fileListItem.path) }.forEach(openFile)
                closeOption()
            }

            if (fileListItem.editable) {
                HoverHighlightDropdownMenuItem(
                    "Edit"
                ) {
                    edit()
                    closeOption()
                }

                HoverHighlightDropdownMenuItem(
                    "Preview"
                ) {
                    preview()
                    closeOption()
                }

                HoverHighlightDropdownMenuItem(
                    "Open background"
                ) {
                    openFile(fileListItem.path)
                    closeOption()
                }

                HoverHighlightDropdownMenuItem(
                    "Slideshow"
                ) {
                    slideshow()
                    closeOption()
                }

                HoverHighlightDropdownMenuItem(
                    "Copy title"
                ) {
                    clipText(fileListItem.path.nameWithoutExtension)
                }

                HoverHighlightDropdownMenuItem(
                    "Clip internal link"
                ) {
                    clipText("[[${fileListItem.path.nameWithoutExtension}]]")
                }
            }
        }
    }
}