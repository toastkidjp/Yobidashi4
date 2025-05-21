package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.ui.text.AnnotatedString
import jp.toastkid.yobidashi4.presentation.lib.text.KeywordHighlighter
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.ByteArrayInputStream
import java.nio.file.Path
import java.util.Base64
import javax.imageio.ImageIO

class MessageContentViewModel : KoinComponent {

    private val keywordHighlighter = KeywordHighlighter()

    private val mainViewModel: MainViewModel by inject()

    fun lineText(listLine: Boolean, text: String): AnnotatedString {
        return keywordHighlighter(if (listLine) text.substring(2) else text)
    }

    fun storeImage(base64Image: String) {
        val image = ByteArrayInputStream(Base64.getDecoder().decode(base64Image))
            .use { ImageIO.read(it) }
        ImageIO.write(image, "png", Path.of("user/download/${System.currentTimeMillis()}.png").toFile())
        mainViewModel
            .showSnackbar("Store image file.", "Open") {
                mainViewModel.openFile(Path.of("user/download"))
            }
    }

}