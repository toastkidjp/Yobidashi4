package jp.toastkid.yobidashi4.presentation.tool.notification

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
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
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
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.presentation.component.SingleLineTextField
import jp.toastkid.yobidashi4.presentation.tool.notification.viewmodel.NotificationListTabViewModel
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun NotificationListTabView() {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = remember { NotificationListTabViewModel() }

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
                        Button(onClick = viewModel::add) {
                            Text("Add")
                        }
                    }
                }

                itemsIndexed(viewModel.items(), { _, item -> item.title + item.text + item.date }) { index, item ->
                    val titleState = remember { (TextFieldState(item.title)) }
                    val textState = remember { (TextFieldState(item.text)) }
                    val dateTimeState = remember { (TextFieldState(item.dateTimeString())) }
                    val headerCursorOn = mutableStateOf(false)
                    val headerColumnBackgroundColor = animateColorAsState(
                        if (headerCursorOn.value) MaterialTheme.colors.primary
                        else if (viewModel.listState().firstVisibleItemIndex != 0) MaterialTheme.colors.surface
                        else Color.Transparent
                    )
                    Row(modifier = Modifier.fillMaxWidth()
                        .animateItem()
                        .onPointerEvent(PointerEventType.Enter) {
                            headerCursorOn.value = true
                        }
                        .onPointerEvent(PointerEventType.Exit) {
                            headerCursorOn.value = false
                        }
                        .drawBehind { drawRect(headerColumnBackgroundColor.value) }
                    ) {
                        NotificationEventRow(titleState)
                        NotificationEventRow(textState)
                        NotificationEventRow(dateTimeState)

                        Button(onClick = {
                            viewModel.update(index, titleState.text, textState.text, dateTimeState.text)
                        }) {
                            Text("Update")
                        }
                        Button(onClick = {
                            viewModel.deleteAt(index)
                        },
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Text("x")
                        }
                    }
                }
            }
            VerticalScrollbar(adapter = rememberScrollbarAdapter(viewModel.listState()), modifier = Modifier.fillMaxHeight().align(
                Alignment.CenterEnd))

            LaunchedEffect(Unit) {
                viewModel.start(Dispatchers.IO)
            }
        }
    }
}

@Composable
private fun NotificationEventRow(
    initialInput: TextFieldState
) {
    Box(
        contentAlignment = Alignment.CenterStart
    ) {
        SingleLineTextField(
            initialInput,
            "Keyword",
            { initialInput.clearText() }
        )
    }
}