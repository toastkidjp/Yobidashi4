/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.tool.file

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import jp.toastkid.yobidashi4.domain.service.tool.file.FileRenamer
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.file.Path

class FileRenameToolViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val paths = mutableStateListOf<Path>()

    private val useResize50Percent = MutableStateFlow(false)

    fun useResize(): StateFlow<Boolean> = useResize50Percent

    fun switchUseResize() {
        useResize50Percent.tryEmit(useResize50Percent.value.not())
    }

    private val listState = LazyListState()

    fun items(): List<Path> = paths

    fun listState() = listState

    private val input = TextFieldState("img")

    fun input() = input

    fun renamedSampleFileName(): String {
        return fileRenamer.makeRenamedFileName(
            input.text,
            paths.size,
            "png"
        )
    }

    private val fileRenamer: FileRenamer by inject()

    fun rename() {
        fileRenamer.invoke(paths, input.text, useResize50Percent.value, {
            viewModel
                .showSnackbar(
                    "Rename completed!",
                    "Open folder",
                    ::openFolder
                )
        })
    }

    private fun openFolder() {
        if (items().isEmpty()) {
            return
        }
        viewModel.openFile(items().first().parent)
    }

    fun onKeyEvent(it: KeyEvent): Boolean {
        if (it.type == KeyEventType.KeyDown && it.key == Key.Enter
            && input.composition == null
            && input.text.isNotBlank()
        ) {
            rename()
            return true
        }

        return false
    }

    fun clearPaths() {
        paths.clear()
    }

    fun collectDroppedPaths() {
        viewModel.registerDroppedPathReceiver(paths::add)
    }

    fun dispose() {
        viewModel.unregisterDroppedPathReceiver()
    }

    fun clearInput() {
        input.clearText()
    }

    fun remove(path: Path) {
        paths.remove(path)
    }

}