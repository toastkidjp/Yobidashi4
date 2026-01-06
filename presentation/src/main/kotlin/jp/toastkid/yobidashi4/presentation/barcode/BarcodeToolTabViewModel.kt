package jp.toastkid.yobidashi4.presentation.barcode

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toComposeImageBitmap
import jp.toastkid.yobidashi4.domain.service.barcode.BarcodeDecoder
import jp.toastkid.yobidashi4.domain.service.barcode.BarcodeEncoder
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.image.BufferedImage
import java.io.IOException
import java.net.URI
import javax.imageio.ImageIO

class BarcodeToolTabViewModel : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    private val barcodeEncoder: BarcodeEncoder by inject()

    private val barcodeDecoder: BarcodeDecoder by inject()

    private val lastBarcode = mutableStateOf<BufferedImage?>(null)

    private val input = TextFieldState()

    private val decodeInput = TextFieldState()

    private val decodeResult = mutableStateOf("")

    fun barcodeImage() =
        lastBarcode.value?.toComposeImageBitmap()

    fun encodeInputValue() = input

    fun setEncodeInputValue() {
        if (input.text.isNotBlank()) {
            lastBarcode.value = barcodeEncoder.invoke(input.text.toString(), 400, 400)
        }
    }

    fun decodeInputValue() = decodeInput

    fun setDecodeInputValue() {
        if (decodeInput.text.isNotBlank()) {
            val image = try {
                ImageIO.read(URI(decodeInput.text.toString()).toURL())
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                null
            } catch (e: IOException) {
                e.printStackTrace()
                null
            } ?: return

            barcodeDecoder.invoke(image)?.let {
                decodeResult.value = it
            }
            lastBarcode.value = image
        }
    }

    fun decodeResult() = decodeResult.value

    fun onClickDecodeResult() {
        mainViewModel.openUrl(decodeResult.value, false)
    }

    fun onClickImage() {
        val image = lastBarcode.value ?: return
        ClipboardPutterService().invoke(image)
        mainViewModel.showSnackbar("Copy barcode to clipboard.")
    }

}