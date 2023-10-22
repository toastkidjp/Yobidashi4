package jp.toastkid.yobidashi4.presentation.viewmodel.barcode

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import java.awt.image.BufferedImage
import java.io.IOException
import java.net.URL
import javax.imageio.ImageIO
import jp.toastkid.yobidashi4.domain.service.barcode.BarcodeDecoder
import jp.toastkid.yobidashi4.domain.service.barcode.BarcodeEncoder
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BarcodeToolTabViewModel {

    private val lastBarcode = mutableStateOf<BufferedImage?>(null)

    private val input = mutableStateOf(TextFieldValue())

    private val decodeInput = mutableStateOf(TextFieldValue())

    private val decodeResult = mutableStateOf("")

    private val mainViewModel = object : KoinComponent { val vm: MainViewModel by inject() }.vm

    private val barcodeEncoder = object : KoinComponent { val it: BarcodeEncoder by inject() }.it

    private val barcodeDecoder = object : KoinComponent { val it: BarcodeDecoder by inject() }.it

    fun barcodeImage() =
        lastBarcode.value?.toComposeImageBitmap()

    fun encodeInputValue() = input.value

    fun setEncodeInputValue(value: TextFieldValue) {
        input.value = value

        if (value.text.isNotBlank()) {
            lastBarcode.value = barcodeEncoder.invoke(value.text, 400, 400)
        }
    }

    fun decodeInputValue() = decodeInput.value

    fun setDecodeInputValue(value: TextFieldValue) {
        decodeInput.value = value

        if (value.text.isNotBlank()) {
            val image = try {
                ImageIO.read(URL(value.text))
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