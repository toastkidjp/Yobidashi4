package jp.toastkid.yobidashi4.presentation.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Divider
import androidx.compose.material.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.tab.InputHistoryTab
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_tab_close
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun InputHistoryView(tab: InputHistoryTab) {
    val viewModel = remember { InputHistoryViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Box {
            LazyColumn(
                state = viewModel.listState(),
                userScrollEnabled = true,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                items(viewModel.items(), key = { it.hashCode() }) { item ->
                    val cursorOn = remember { mutableStateOf(false) }
                    val backgroundColor = animateColorAsState(if (cursorOn.value) MaterialTheme.colors.primary else Color.Transparent)

                    Box(
                        Modifier.pointerInput(Unit) {
                            awaitEachGesture {
                                viewModel.onPointerEvent(awaitPointerEvent(), item)
                            }
                        }
                            .animateItem()
                            .drawBehind { drawRect(backgroundColor.value) }
                            .onPointerEvent(PointerEventType.Enter) {
                                cursorOn.value = true
                            }
                            .onPointerEvent(PointerEventType.Exit) {
                                cursorOn.value = false
                            }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier
                                .combinedClickable(
                                    enabled = true,
                                    onClick = {
                                        viewModel.open(item)
                                    },
                                    onLongClick = {
                                        viewModel.openOnBackground(item)
                                    }
                                )
                                .padding(horizontal = 16.dp)
                            ) {
                                val textColor = if (cursorOn.value) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface

                                Text(item.word, color = textColor)
                                Text(
                                    viewModel.dateTimeString(item),
                                    color = textColor,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))
                            }
                        }

                        Icon(
                            painterResource(Res.drawable.ic_tab_close),
                            contentDescription = "Delete item ${item.word}",
                            modifier = Modifier.clickable {
                                viewModel.delete(item)
                            }
                        )
                    }
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(viewModel.listState()),
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
            )

            val coroutineScope = rememberCoroutineScope()
            DisposableEffect(tab) {
                viewModel.launch(coroutineScope, tab)

                onDispose {
                    viewModel.onDispose(tab)
                }
            }
        }
    }
}