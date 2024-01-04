package jp.toastkid.yobidashi4.presentation.tool.notification

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import jp.toastkid.yobidashi4.presentation.tool.notification.viewmodel.NotificationListTabViewModel
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalFoundationApi::class)
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
                        Button(onClick = {
                            viewModel.add()
                        }) {
                            Text("Add")
                        }
                    }
                }

                itemsIndexed(viewModel.items()) { index, item ->
                    Row(modifier = Modifier.fillMaxWidth().animateItemPlacement()) {
                        var notificationEvent = item
                        NotificationEventRow(item.title, viewModel.listState().firstVisibleItemIndex != 0) {
                            if (it.composition != null) {
                                return@NotificationEventRow
                            }

                            notificationEvent = notificationEvent.copy(title = it.text)
                        }
                        NotificationEventRow(item.text, viewModel.listState().firstVisibleItemIndex != 0) {
                            if (it.composition != null) {
                                return@NotificationEventRow
                            }

                            notificationEvent = notificationEvent.copy(text = it.text)
                        }
                        NotificationEventRow(item.dateTimeString(), viewModel.listState().firstVisibleItemIndex != 0) {
                            if (it.composition != null) {
                                return@NotificationEventRow
                            }

                            val dateTime = NotificationEvent.parse(it.text) ?: return@NotificationEventRow

                            notificationEvent = notificationEvent.copy(date = dateTime)
                        }

                        Button(onClick = {
                            viewModel.update(index, notificationEvent)
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun NotificationEventRow(
    initialInput: String,
    firstVisible: Boolean,
    onValueChange: (TextFieldValue) -> Unit
) {
    val input = remember { mutableStateOf(TextFieldValue()) }

    val headerCursorOn = mutableStateOf(false)
    val headerColumnBackgroundColor = animateColorAsState(
        if (headerCursorOn.value) MaterialTheme.colors.primary
        else if (firstVisible) MaterialTheme.colors.surface
        else Color.Transparent
    )

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier.Companion
            .onPointerEvent(PointerEventType.Enter) {
                headerCursorOn.value = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                headerCursorOn.value = false
            }
            .drawBehind { drawRect(headerColumnBackgroundColor.value) }
    ) {
        TextField(
            input.value,
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(
                textColor = if (headerCursorOn.value) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
                backgroundColor = Color.Transparent,
                cursorColor = MaterialTheme.colors.secondary
            ),
            label = { Text("Keyword", color = MaterialTheme.colors.secondary) },
            onValueChange = {
                input.value = it
                onValueChange(it)
            },
            keyboardActions = KeyboardActions(
                onDone = {
                }
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            trailingIcon = {
                Icon(
                    painterResource("images/icon/ic_clear_form.xml"),
                    contentDescription = "Clear input.",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.clickable {
                        input.value = TextFieldValue()
                    }
                )
            }
        )

        LaunchedEffect(onValueChange) {
            input.value = TextFieldValue(initialInput)
        }
    }
}