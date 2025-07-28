package jp.toastkid.yobidashi4.presentation.log.viewer

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import jp.toastkid.yobidashi4.presentation.lib.KeyboardScrollAction
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicReference

class TextFileViewerTabViewModel : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    private val listState = LazyListState()

    private val textState = mutableStateListOf<String>()

    private val keyboardScrollAction = KeyboardScrollAction(listState)

    private val focusRequester = FocusRequester()

    private val lastPath = AtomicReference<Path>()

    fun keyboardScrollAction(coroutineScope: CoroutineScope, key: Key, isCtrlPressed: Boolean): Boolean {
        if (isCtrlPressed && key == Key.O) {
            val path = lastPath.get()
            mainViewModel.openFile(path)
            return true
        }

        return keyboardScrollAction.invoke(coroutineScope, key, isCtrlPressed)
    }

    fun focusRequester(): FocusRequester = focusRequester

    fun listState() = listState

    fun textState(): List<String> = textState

    fun lineNumber(index: Int): String {
        val length = textState.size.toString().length
        val lineNumberCount = index + 1
        val fillCount = length - lineNumberCount.toString().length
        return with(StringBuilder()) {
            repeat(fillCount) {
                append(" ")
            }
            append(lineNumberCount)
        }.toString()
    }

    suspend fun launch(path: Path, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        if (Files.exists(path).not()) {
            return
        }

        lastPath.set(path)

        withContext(dispatcher) {
            try {
                Files.readAllLines(path).forEach { textState.add(it) }
            } catch (e: IOException) {
                LoggerFactory.getLogger(javaClass).error("File read error.", e)
            }
        }

        focusRequester().requestFocus()
    }

}