package jp.toastkid.yobidashi4.infrastructure.service.barcode

import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.awt.image.BufferedImage
import java.awt.image.WritableRaster
import jp.toastkid.yobidashi4.domain.service.barcode.BarcodeDecoder
import org.koin.core.annotation.Single

@Single
class BarcodeDecoderImplementation : BarcodeDecoder {

    private val reader: MultiFormatReader = MultiFormatReader().also {
        it.setHints(
            mapOf(
                DecodeHintType.POSSIBLE_FORMATS to arrayListOf(
                    BarcodeFormat.QR_CODE,
                    BarcodeFormat.EAN_13
                )
            )
        )
    }

    override fun invoke(sourceImage: BufferedImage): String? {
        val sourceWidth = sourceImage.width
        val sourceHeight = sourceImage.height

        val image = BufferedImage(sourceWidth, sourceHeight, BufferedImage.TYPE_BYTE_GRAY)

        val raster: WritableRaster = image.getRaster()
        val buffer = IntArray(sourceImage.width)
        for (y in 0 until sourceImage.height) {
            sourceImage.getRGB(0, y, sourceImage.width, 1, buffer, 0, sourceWidth)
            for (x in 0 until sourceImage.width) {
                val pixel = buffer[x]

                if (pixel and -0x1000000 == 0) {
                    buffer[x] = 0xFF
                } else {
                    buffer[x] = 306 * (pixel shr 16 and 0xFF) + 601 * (pixel shr 8 and 0xFF) + 117 * (pixel and 0xFF) +
                            0x200 shr 10
                }
            }
            raster.setPixels(0, y, sourceImage.width, 1, buffer)
        }
        val matrix = ByteArray(sourceWidth * sourceHeight)
        raster.getDataElements(0, 0, sourceImage.width, sourceImage.height, matrix)

        return reader.decodeWithState(
            BinaryBitmap(
                HybridBinarizer(
                    PlanarYUVLuminanceSource(
                        matrix,
                        sourceWidth,
                        sourceHeight,
                        0,
                        0,
                        sourceWidth,
                        sourceHeight,
                        false
                    )
                )
            )
        ).text
    }

}