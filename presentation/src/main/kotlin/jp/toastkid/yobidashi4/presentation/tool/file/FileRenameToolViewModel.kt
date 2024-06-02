package jp.toastkid.yobidashi4.presentation.tool.file

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.TextFieldValue
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.extension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FileRenameToolViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val paths = mutableStateListOf<Path>()

    private val input = mutableStateOf(TextFieldValue("img_"))

    private val listState = LazyListState()

    fun items(): List<Path> = paths

    fun listState() = listState

    fun input() = input.value

    fun onValueChange(it: TextFieldValue) {
        input.value = it
    }

    fun rename() {
        if (paths.isEmpty()) {
            return
        }

        paths.forEachIndexed { i, p ->
            Files.copy(p, p.resolveSibling("${input.value.text}_${i + 1}.${p.extension}"))
        }

        viewModel
            .showSnackbar(
                "Rename completed!",
                "Open folder",
                { viewModel.openFile(paths.first().parent) }
            )
    }

    fun onKeyEvent(it: KeyEvent): Boolean {
        if (it.type == KeyEventType.KeyDown && it.key == Key.Enter
            && input.value.composition == null
            && input.value.text.isNotBlank()
        ) {
            rename()
            return true
        }

        return false
    }

    fun clearPaths() {
        paths.clear()
    }

    fun launchedEffectKey() = viewModel.droppedPathFlow().hashCode()

    suspend fun collectDroppedPaths() {
        viewModel.droppedPathFlow().collect {
            paths.add(it)
        }
    }

    fun clearInput() {
        input.value = TextFieldValue()
    }

}