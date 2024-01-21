package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.text.input.TextFieldValue
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.service.archive.ZipArchiver
import jp.toastkid.yobidashi4.presentation.main.content.data.FileListItem
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FileListViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val completeItems = mutableStateListOf<FileListItem>()

    private val articleStates = mutableStateListOf<FileListItem>()

    private val listState = LazyListState()

    private val horizontalScrollState = ScrollState(0)

    private var controlPressing = false

    private var shiftPressing = false

    private val keyword = mutableStateOf(TextFieldValue())

    fun listState() = listState

    fun horizontalScrollState() = horizontalScrollState

    fun keyword() = keyword.value

    fun start(paths: List<Path>) {
        articleStates.clear()
        paths.map {
            FileListItem(it, editable = editableExtensions.contains(it.extension)) }.forEach { articleStates.add(it) }
        completeItems.addAll(articleStates)
    }

    fun onKeyEvent(coroutineScope: CoroutineScope, keyEvent: KeyEvent): Boolean {
        controlPressing = keyEvent.isCtrlPressed
        shiftPressing = keyEvent.isShiftPressed

        if (keyEvent.isCtrlPressed && keyEvent.key == Key.Z) {
            ZipArchiver().invoke(articleStates.filter { it.selected }.map { it.path })
            viewModel.openFile(Path.of("."))
            return@onKeyEvent true
        }
        if (keyEvent.key == Key.DirectionUp) {
            coroutineScope.launch {
                listState.scrollToItem(max(0, listState.firstVisibleItemIndex - 1), 0)
            }
            return@onKeyEvent true
        }
        if (keyEvent.key == Key.DirectionDown) {
            coroutineScope.launch {
                listState.scrollToItem(min(articleStates.size - 1, listState.firstVisibleItemIndex + 1), 0)
            }
            return@onKeyEvent true
        }

        return false
    }

    fun currentIsTop(): Boolean {
        return listState.firstVisibleItemIndex == 0
    }

    fun onValueChange(it: TextFieldValue) {
        keyword.value = it
        if (keyword.value.composition == null) {
            articleStates.clear()
            articleStates.addAll(
                if (keyword.value.text.isNotBlank()) completeItems.filter { item -> item.path.nameWithoutExtension.lowercase().contains(keyword.value.text.lowercase()) }
                else completeItems
            )
        }
    }

    fun clearInput() {
        keyword.value = TextFieldValue()
        articleStates.clear()
        articleStates.addAll(completeItems)
    }

    fun items() = articleStates

    fun onSingleClick(fileListItem: FileListItem) {
        val clickedIndex = items().indexOf(fileListItem)

        if (shiftPressing) {
            val startIndex = articleStates.indexOfFirst { it.selected }
            val range =
                if (startIndex < clickedIndex) (startIndex + 1)..clickedIndex else (clickedIndex until startIndex)
            range.forEach { targetIndex ->
                articleStates.set(targetIndex, articleStates.get(targetIndex).reverseSelection())
            }
            return
        }

        if (controlPressing.not() && shiftPressing.not()) {
            articleStates.mapIndexed { i, f -> i to f }
                .filter { it.second.selected }
                .forEach {
                    articleStates.set(it.first, it.second.unselect())
                }
        }

        articleStates.set(clickedIndex, fileListItem.reverseSelection())
    }

    fun onLongClick(fileListItem: FileListItem) {
        if (fileListItem.editable) {
            viewModel.edit(fileListItem.path, true)
        } else {
            viewModel.openFile(fileListItem.path)
        }
    }

    fun onDoubleClick(fileListItem: FileListItem) {
        if (fileListItem.editable) {
            viewModel.hideArticleList()
            viewModel.edit(fileListItem.path)
        } else {
            viewModel.openFile(fileListItem.path)
        }
    }

}

private val editableExtensions = setOf("md", "txt")
