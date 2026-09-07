package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.foundation.ContextMenuState
import androidx.compose.foundation.ScrollState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.AnnotatedString
import jp.toastkid.yobidashi4.domain.model.download.DownloadFolder
import jp.toastkid.yobidashi4.presentation.lib.text.KeywordHighlighter
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.file.Path
import java.util.Base64
import java.util.concurrent.atomic.AtomicReference
import javax.imageio.ImageIO

@Factory
class MessageContentViewModel(
    private val mainViewModel: MainViewModel
) : KoinComponent {

    private val keywordHighlighter = KeywordHighlighter()

    private val imageHolder = AtomicReference<ImageBitmap>(EMPTY_IMAGE)

    private val contextMenuState = ContextMenuState()

    private val horizontalSourceScrollState = ScrollState(0)

    fun lineText(listLine: Boolean, text: String): AnnotatedString {
        return keywordHighlighter(if (listLine) text.substring(2) else text)
    }

    fun image(base64Image: String): ImageBitmap {
        val current = imageHolder.get()
        if (current != EMPTY_IMAGE) {
            return current
        }

        try {
            val loadImage = loadImage(base64Image)
            imageHolder.set(loadImage)

            return imageHolder.get()
        } catch (e: Exception) {
            LoggerFactory.getLogger(javaClass).warn("Image loading error.", e)
            return imageHolder.get()
        }
    }

    fun storeImage(base64Image: String) {
        val image = image(base64Image)
        ImageIO.write(image.toAwtImage(), "png", Path.of("user/download/${System.currentTimeMillis()}.png").toFile())
        mainViewModel
            .showSnackbar("Store image file.", "Open") {
                mainViewModel.openFile(DownloadFolder().getPath())
            }
    }

    @Throws(IllegalArgumentException::class, IOException::class)
    private fun loadImage(base64Image: String): ImageBitmap {
        return ByteArrayInputStream(Base64.getDecoder().decode(base64Image))
            .use(ImageIO::read)
            .toComposeImageBitmap()
    }

    fun contextMenuState() = contextMenuState

    fun openLink(url: String) {
        mainViewModel.openUrl(url, false)
    }

    fun openLinkOnBackground(url: String) {
        mainViewModel.openUrl(url, true)
    }

    fun horizontalSourceScrollState() = horizontalSourceScrollState

}

private val EMPTY_IMAGE = ImageBitmap(1, 1)
