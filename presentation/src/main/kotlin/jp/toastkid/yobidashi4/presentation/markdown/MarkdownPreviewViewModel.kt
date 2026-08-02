package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.koin.core.component.KoinComponent
import java.io.IOException
import java.net.URI
import javax.imageio.ImageIO

class MarkdownPreviewViewModel(scrollState: ScrollableState) : KoinComponent {

    private val scrollEventFlow = MutableSharedFlow<Float>(extraBufferCapacity = 1)

    fun scrollEventFlow(): SharedFlow<Float> = scrollEventFlow

    fun onKeyEvent(coroutineScope: CoroutineScope, it: KeyEvent): Boolean {
        return when (it.key) {
            Key.DirectionUp -> {
                val max = if (it.isCtrlPressed) Float.MAX_VALUE else 50f
                scrollEventFlow.tryEmit(-1f * max)
                true
            }
            Key.DirectionDown -> {
                val max = if (it.isCtrlPressed) Float.MAX_VALUE else 50f
                scrollEventFlow.tryEmit(max)
                true
            }
            Key.PageUp -> {
                scrollEventFlow.tryEmit(-300f)
                true
            }
            Key.PageDown -> {
                scrollEventFlow.tryEmit(300f)
                true
            }
            else -> false
        }
    }

    fun extractText(it: String, taskList: Boolean): String {
        return if (taskList) it.substring(it.indexOf("] ") + 1) else it
    }

    fun loadBitmap(source: String): ImageBitmap? {
        val bufferedImage = try {
            ImageIO.read(URI(source).toURL())
        } catch (e: IllegalArgumentException) {
            return null
        } catch (e: IOException) {
            return null
        } ?: return null

        return bufferedImage.toComposeImageBitmap()
    }

    fun makeFontWeight(level: Int): FontWeight {
        return if (level != -1) FontWeight.Bold else FontWeight.Normal
    }

    private val showSubheadings = mutableStateOf(false)

    fun showSubheadings() = showSubheadings.value

    fun switchSubheadings() {
        showSubheadings.value = showSubheadings.value.not()
    }

}
