/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import jp.toastkid.yobidashi4.domain.service.archive.ZipArchiver
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.main.content.data.FileListItem
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension
import kotlin.math.max
import kotlin.math.min

class FileListViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val completeItems = mutableListOf<FileListItem>()

    private val articleStates = mutableStateListOf<FileListItem>()

    private val listState = LazyListState()

    private val horizontalScrollState = ScrollState(0)

    private val controlPressing = AtomicBoolean(false)

    private val shiftPressing = AtomicBoolean(false)

    private val keyword = TextFieldState()

    fun listState() = listState

    fun horizontalScrollState() = horizontalScrollState

    fun keyword() = keyword

    fun start(paths: List<Path>) {
        articleStates.clear()
        paths
            .map { FileListItem(it, editable = editableExtensions.contains(it.extension)) }
            .sortedByDescending(FileListItem::sortKey)
            .forEach(articleStates::add)
        completeItems.clear()
        completeItems.addAll(articleStates)
    }

    fun onKeyEvent(coroutineScope: CoroutineScope, keyEvent: KeyEvent): Boolean {
        controlPressing.set(keyEvent.isCtrlPressed)
        shiftPressing.set(keyEvent.isShiftPressed)

        if (keyEvent.type != KeyEventType.KeyDown) {
            return false
        }

        if (keyEvent.isCtrlPressed && keyEvent.key == Key.Z) {
            ZipArchiver().invoke(articleStates.filter(FileListItem::selected).map(FileListItem::path))
            viewModel.openFile(Path.of("."))
            return true
        }
        if (keyEvent.key == Key.DirectionUp) {
            coroutineScope.launch {
                listState.scrollToItem(max(0, listState.firstVisibleItemIndex - 1), 0)
            }
            return true
        }
        if (keyEvent.key == Key.DirectionDown) {
            coroutineScope.launch {
                listState.scrollToItem(min(items().size - 1, listState.firstVisibleItemIndex + 1), 0)
            }
            return true
        }

        return false
    }

    fun onKeyEventFromCell(it: KeyEvent, fileListItem: FileListItem): Boolean {
        if (it.key == Key.Enter) {
            if (fileListItem.editable) {
                edit(fileListItem.path)
                viewModel.hideArticleList()
            } else {
                viewModel.openFile(fileListItem.path)
            }
            return true
        }
        return false
    }

    fun currentIsTop(): Boolean {
        return listState.firstVisibleItemIndex == 0
    }

    fun onValueChange() {
        if (keyword.composition != null) {
            return
        }

        val lowercase = keyword.text.toString().lowercase()

        articleStates.clear()
        articleStates.addAll(
            if (lowercase.isNotBlank())
                completeItems.filter { it.keep() }.filter { item -> item.path.nameWithoutExtension.lowercase().contains(lowercase) }
            else
                completeItems.filter { it.keep() }
        )
    }

    fun clearInput() {
        keyword.clearText()
        articleStates.clear()
        articleStates.addAll(completeItems)
    }

    fun items(): List<FileListItem> = articleStates

    fun selectedFiles() = articleStates.filter(FileListItem::selected).map(FileListItem::path)

    fun onSingleClick(fileListItem: FileListItem) {
        val clickedIndex = items().indexOf(fileListItem)

        val shift = shiftPressing.get()
        if (shift) {
            val startIndex = articleStates.indexOfFirst(FileListItem::selected)
            val range =
                if (startIndex < clickedIndex) (startIndex + 1)..clickedIndex
                else (clickedIndex until startIndex)
            range.forEach { targetIndex ->
                articleStates.set(targetIndex, articleStates.get(targetIndex).reverseSelection())
            }
            return
        }

        if (controlPressing.get().not()) {
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
        if (fileListItem.editable.not()) {
            viewModel.openFile(fileListItem.path)
            return
        }

        viewModel.hideArticleList()
        viewModel.edit(fileListItem.path)
    }

    private val currentDropdownItem = mutableStateOf<FileListItem?>(null)

    fun openingDropdown(item: FileListItem) = currentDropdownItem.value == item

    fun openDropdown(item: FileListItem) {
        currentDropdownItem.value = item
    }

    fun closeDropdown() {
        currentDropdownItem.value = null
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun onPointerEvent(pointerEvent: PointerEvent, index: Int) {
        val fileListItem = items().getOrNull(index) ?: return
        if (pointerEvent.type == PointerEventType.Press
            && pointerEvent.button == PointerButton.Secondary
            && !openingDropdown(fileListItem)
        ) {
            openDropdown(fileListItem)
        }
    }

    fun openFile(path: Path? = null) {
        useSelectedFile(path, viewModel::openFile)
    }

    fun edit(path: Path) {
        useSelectedFile(path, viewModel::edit)
    }

    fun preview(path: Path) {
        useSelectedFile(path, viewModel::openPreview)
    }

    private fun useSelectedFile(pathIfEmpty: Path?, action: (Path) -> Unit) {
        articleStates.filter(FileListItem::selected)
            .map(FileListItem::path)
            .ifEmpty { makeSubstitute(pathIfEmpty) }
            .forEach(action)
    }

    private fun makeSubstitute(pathIfEmpty: Path?) =
        if (pathIfEmpty == null) emptyList()
        else listOf(pathIfEmpty)

    fun slideshow(path: Path) {
        viewModel.slideshow(path)
    }

    fun clipText(text: String) {
        ClipboardPutterService().invoke(text)
        closeDropdown()
    }

}

private val editableExtensions = setOf("md", "txt")
