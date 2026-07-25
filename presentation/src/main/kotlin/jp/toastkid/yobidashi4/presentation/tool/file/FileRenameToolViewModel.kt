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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import java.text.DecimalFormat
import javax.imageio.ImageIO
import kotlin.io.path.extension
import kotlin.math.max

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
        return makeRenamedFileName(
            DecimalFormat("0".repeat(paths.size.toString().length)),
            paths.size,
            "png"
        )
    }

    /**
     * 画像の縦横サイズを半分にして保存する関数
     * @return 処理が成功したかどうか
     */
    fun resizeImageToHalf(sourcePath: Path, targetPath: Path): Boolean {
        val originalImage: BufferedImage = ImageIO.read(sourcePath.toFile()) ?: return false

        val newWidth = max(1, originalImage.width / 2)
        val newHeight = max(1, originalImage.height / 2)

        val resizedImage = BufferedImage(newWidth, newHeight, originalImage.type.let {
            if (it == BufferedImage.TYPE_CUSTOM) BufferedImage.TYPE_INT_ARGB else it
        })

        val g2d = resizedImage.createGraphics()
        try {
            g2d.setRenderingHint(
                java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR
            )
            g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null)
        } finally {
            g2d.dispose()
        }

        val formatName = sourcePath.extension.ifEmpty { "png" }
        return ImageIO.write(resizedImage, formatName, targetPath.toFile())
    }

    fun rename() {
        if (paths.isEmpty()) {
            return
        }

        val decimalFormat = DecimalFormat("0".repeat(paths.size.toString().length))
        CoroutineScope(Dispatchers.IO).launch {
            paths.forEachIndexed { i, p ->
                if (useResize50Percent.value) {
                    resizeImageToHalf(p, p.resolveSibling(makeRenamedFileName(decimalFormat, i, p.extension)))
                    return@forEachIndexed
                }

                Files.copy(p, p.resolveSibling(makeRenamedFileName(decimalFormat, i, p.extension)))
            }

            withContext(Dispatchers.Unconfined) {
                viewModel
                    .showSnackbar(
                        "Rename completed!",
                        "Open folder",
                        ::openFolder
                    )
            }
        }
    }

    private fun makeRenamedFileName(decimalFormat: DecimalFormat, i: Int, extension: String): String =
        "${input.text}_${decimalFormat.format(i + 1)}.$extension"

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