package jp.toastkid.yobidashi4.presentation.setting

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
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
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.presentation.component.SingleLineTextField

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SettingEditorView() {
    val viewModel = remember { SettingEditorViewModel() }
    val coroutineScope = rememberCoroutineScope()

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp,
        modifier = Modifier.onKeyEvent {
            return@onKeyEvent viewModel.onKeyEvent(coroutineScope, it)
        }.focusRequester(viewModel.focusRequester()).focusable(true)
    ) {
        Box {
            LazyColumn(
                state = viewModel.listState(),
                userScrollEnabled = true
            ) {
                stickyHeader {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = viewModel::save) {
                            Text("Save")
                        }
                        Button(onClick = viewModel::openFile, Modifier.padding(start = 4.dp)) {
                            Text("Open")
                        }
                    }
                }

                itemsIndexed(viewModel.items(), { _, item -> item.first }) { _, item ->
                    val cursorOn = mutableStateOf(false)
                    val columnBackgroundColor = animateColorAsState(
                        if (cursorOn.value) MaterialTheme.colors.primary
                        else if (viewModel.listState().firstVisibleItemIndex != 0) MaterialTheme.colors.surface
                        else Color.Transparent
                    )
                    Row(modifier = Modifier.fillMaxWidth().animateItem()
                        .onPointerEvent(PointerEventType.Enter) {
                            cursorOn.value = true
                        }
                        .onPointerEvent(PointerEventType.Exit) {
                            cursorOn.value = false
                        }
                        .drawBehind { drawRect(columnBackgroundColor.value) }
                    ) {
                        val state = mutableStateOf(item.second)
                        SingleLineTextField(
                            state.value,
                            item.first,
                            {
                                state.value = it
                                viewModel.update(item.first, it)
                            },
                            {
                                state.value = TextFieldValue()
                                viewModel.update(item.first, TextFieldValue())
                            }
                        )
                    }
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(viewModel.listState()),
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.start()
    }
}