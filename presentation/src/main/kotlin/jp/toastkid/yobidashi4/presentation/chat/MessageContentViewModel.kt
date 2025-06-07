package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.foundation.ContextMenuState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.AnnotatedString
import jp.toastkid.yobidashi4.presentation.lib.text.KeywordHighlighter
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.ByteArrayInputStream
import java.nio.file.Path
import java.util.Base64
import java.util.concurrent.atomic.AtomicReference
import javax.imageio.ImageIO

class MessageContentViewModel : KoinComponent {

    private val keywordHighlighter = KeywordHighlighter()

    private val mainViewModel: MainViewModel by inject()

    private val imageHolder = AtomicReference<ImageBitmap>()

    private val contextMenuState = ContextMenuState()

    fun lineText(listLine: Boolean, text: String): AnnotatedString {
        return keywordHighlighter(if (listLine) text.substring(2) else text)
    }

    fun image(base64Image: String): ImageBitmap {
        val current = imageHolder.get()
        if (current != null) {
            return current
        }

        val loadImage = loadImage(base64Image)
        imageHolder.set(loadImage)

        return imageHolder.get()
    }

    fun storeImage(base64Image: String) {
        val image = image(base64Image)
        ImageIO.write(image.toAwtImage(), "png", Path.of("user/download/${System.currentTimeMillis()}.png").toFile())
        mainViewModel
            .showSnackbar("Store image file.", "Open") {
                mainViewModel.openFile(Path.of("user/download"))
            }
    }

    private fun loadImage(base64Image: String): ImageBitmap {
        return ByteArrayInputStream(Base64.getDecoder().decode(base64Image))
            .use { ImageIO.read(it) }
            .toComposeImageBitmap()
    }

    fun contextMenuState() = contextMenuState

}