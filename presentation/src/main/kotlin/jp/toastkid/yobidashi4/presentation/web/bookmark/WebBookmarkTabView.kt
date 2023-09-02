package jp.toastkid.yobidashi4.presentation.web.bookmark

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import jp.toastkid.yobidashi4.presentation.component.LoadIcon
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.absolutePathString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun WebBookmarkTabView() {
    val koin = object : KoinComponent {
        val viewModel: MainViewModel by inject()
        val repo: BookmarkRepository by inject()
    }
    val viewModel = remember { koin.viewModel }

    val bookmarks = remember {
        val list = mutableStateListOf<Bookmark>()
        val repository = koin.repo
        repository.list().forEach { list.add(it) }
        list
    }

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
                items(bookmarks) { bookmark ->
                    WebBookmarkItemRow(
                        bookmark,
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
                adapter = rememberScrollbarAdapter(state),
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
private fun WebBookmarkItemRow(
    bookmark: Bookmark,
    modifier: Modifier
) {
    val cursorOn = remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                if (cursorOn.value) MaterialTheme.colors.primary else Color.Transparent
            )
            .onPointerEvent(PointerEventType.Enter) {
                cursorOn.value = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                cursorOn.value = false
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
}