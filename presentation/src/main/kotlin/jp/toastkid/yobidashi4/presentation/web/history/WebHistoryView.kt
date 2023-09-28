package jp.toastkid.yobidashi4.presentation.web.history

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.net.MalformedURLException
import java.net.URL
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import jp.toastkid.yobidashi4.domain.model.web.history.WebHistory
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.domain.repository.web.history.WebHistoryRepository
import jp.toastkid.yobidashi4.presentation.component.LoadIcon
import jp.toastkid.yobidashi4.presentation.lib.KeyboardScrollAction
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.absolutePathString
import kotlin.io.path.pathString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun WebHistoryView() {
    val viewModel = remember { object : KoinComponent { val viewModel: MainViewModel by inject() }.viewModel }

    val favicons = remember { WebIcon().readAll() }

    val webHistories = remember {
        val list = mutableStateListOf<WebHistory>()
        val repository = object : KoinComponent { val repository: WebHistoryRepository by inject() }.repository
        repository.readAll().sortedByDescending { it.lastVisitedTime }.forEach { list.add(it) }
        list
    }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd(E)HH:mm:ss").withLocale(Locale.ENGLISH) }

    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val state = rememberLazyListState()
    val scrollAction = remember { KeyboardScrollAction(state) }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp,
        modifier = Modifier.onKeyEvent {
            return@onKeyEvent scrollAction.invoke(coroutineScope, it.key, it.isCtrlPressed)
        }.focusRequester(focusRequester).focusable(true)
    ) {
        Box {
            LazyColumn(
                state = state,
                userScrollEnabled = true,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                items(webHistories) { bookmark ->
                    val cursorOn = remember { mutableStateOf(false) }
                    val backgroundColor = animateColorAsState(if (cursorOn.value) MaterialTheme.colors.primary else Color.Transparent)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.animateItemPlacement()
                            .drawBehind { drawRect(backgroundColor.value) }
                            .onPointerEvent(PointerEventType.Enter) {
                                cursorOn.value = true
                            }
                            .onPointerEvent(PointerEventType.Exit) {
                                cursorOn.value = false
                            }
                    ) {
                        val iconPath = favicons.firstOrNull {
                            val host = extractHost(bookmark) ?: return@firstOrNull false
                            val startsWith = it.fileName.pathString.startsWith(host)
                            startsWith
                        }
                        LoadIcon(iconPath?.absolutePathString(), Modifier.size(32.dp).padding(start = 4.dp).padding(horizontal = 4.dp))
                        Column(modifier = Modifier
                            .combinedClickable(
                                enabled = true,
                                onClick = {
                                    viewModel.openUrl(bookmark.url, false)
                                },
                                onLongClick = {
                                    viewModel.openUrl(bookmark.url, true)
                                }
                            )
                            .padding(horizontal = 16.dp)
                            .animateItemPlacement()
                        ) {
                            val textColor = if (cursorOn.value) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface

                            Text(bookmark.title, color = textColor)
                            Text(bookmark.url, maxLines = 1, overflow = TextOverflow.Ellipsis, color = textColor)
                            Text(
                                LocalDateTime
                                    .ofInstant(Instant.ofEpochMilli(bookmark.lastVisitedTime), ZoneId.systemDefault())
                                    .format(dateFormatter),
                                color = textColor,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))
                        }
                    }
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(state),
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
            )

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }
}

private fun extractHost(bookmark: WebHistory): String? {
    return try {
        URL(bookmark.url).host.trim()
    } catch (e: MalformedURLException) {
        null
    }
}