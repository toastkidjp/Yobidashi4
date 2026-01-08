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
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.file.Files
import java.nio.file.Path
import java.text.DecimalFormat
import kotlin.io.path.extension

class FileRenameToolViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val paths = mutableStateListOf<Path>()

    private val input = TextFieldState("img")

    private val listState = LazyListState()

    fun items(): List<Path> = paths

    fun listState() = listState

    fun input() = input

    fun rename() {
        if (paths.isEmpty()) {
            return
        }

        val decimalFormat = DecimalFormat("0".repeat(paths.size.toString().length))
        paths.forEachIndexed { i, p ->
            Files.copy(p, p.resolveSibling("${input.text}_${decimalFormat.format(i + 1)}.${p.extension}"))
        }

        viewModel
            .showSnackbar(
                "Rename completed!",
                "Open folder",
                ::openFolder
            )
    }

    private fun openFolder() {
        viewModel.openFile(paths.first().parent)
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
        viewModel.registerDroppedPathReceiver {
            paths.add(it)
        }
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