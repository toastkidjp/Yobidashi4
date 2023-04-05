package jp.toastkid.yobidashi4.presentation.web.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.stream.Collectors
import jp.toastkid.yobidashi4.domain.model.web.history.WebHistory
import jp.toastkid.yobidashi4.domain.repository.web.history.WebHistoryRepository
import jp.toastkid.yobidashi4.presentation.component.LoadIcon
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.absolutePathString
import kotlin.io.path.pathString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WebHistoryView() {
    val koin = object : KoinComponent {
        val viewModel: MainViewModel by inject()
        val repo: WebHistoryRepository by inject()
    }

    val viewModel = koin.viewModel

    val webHistories = remember {
        val list = mutableStateListOf<WebHistory>()
        val repository = koin.repo
        repository.readAll().sortedByDescending { it.lastVisitedTime }.forEach { list.add(it) }
        list
    }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd(E)HH:mm:ss").withLocale(Locale.ENGLISH) }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Box {
            val state = rememberLazyListState()
            LazyColumn(
                state = state,
                userScrollEnabled = true,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                items(webHistories) { bookmark ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.animateItemPlacement()) {
                        val faviconFolder = Paths.get("data/web/icon")
                        val iconPath = Files.list(faviconFolder).collect(Collectors.toList()).firstOrNull {
                            val startsWith = it.fileName.pathString.startsWith(URL(bookmark.url).host.trim())
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
                            Text(bookmark.title)
                            Text(bookmark.url, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(
                                LocalDateTime
                                    .ofInstant(Instant.ofEpochMilli(bookmark.lastVisitedTime), ZoneId.systemDefault())
                                    .format(dateFormatter),
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
        }
    }
}