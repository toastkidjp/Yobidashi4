package jp.toastkid.yobidashi4.presentation.log.viewer

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import jp.toastkid.yobidashi4.presentation.lib.keyboard.KeyboardDrivenScrollEventHandler
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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

    private val focusRequester = FocusRequester()

    private val lastPath = AtomicReference<Path>()

    private val keyboardDrivenScrollEventHandler = KeyboardDrivenScrollEventHandler()

    private val scrollEventFlow = MutableSharedFlow<Float>(extraBufferCapacity = 1)

    fun scrollEventFlow(): SharedFlow<Float> = scrollEventFlow

    fun keyboardScrollAction(keyEvent: KeyEvent): Boolean {
        if (keyEvent.type != KeyEventType.KeyDown) {
            return false
        }

        if (keyEvent.isCtrlPressed && keyEvent.key == Key.O) {
            val path = lastPath.get()
            mainViewModel.openFile(path)
            return true
        }

        val keyboardDrivenScrollResult = keyboardDrivenScrollEventHandler.invoke(keyEvent)
        if (keyboardDrivenScrollResult.delta != -1f) {
            scrollEventFlow.tryEmit(keyboardDrivenScrollResult.delta)
        }
        return keyboardDrivenScrollResult.consumed
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
                Files.readAllLines(path).forEach(textState::add)
            } catch (e: IOException) {
                LoggerFactory.getLogger(javaClass).error("File read error.", e)
            }
        }

        focusRequester().requestFocus()
    }

}
