package jp.toastkid.yobidashi4.infrastructure.service.barcode

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitArray
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.awt.image.BufferedImage
import jp.toastkid.yobidashi4.domain.service.barcode.BarcodeEncoder
import org.koin.core.annotation.Single

@Single
class BarcodeEncoderImplementation : BarcodeEncoder {

    @Throws(WriterException::class)
    override operator fun invoke(contents: String?, width: Int, height: Int): BufferedImage? {
        val writer = QRCodeWriter()
        val hints = hashMapOf(EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M)

        val bitMatrix: BitMatrix = writer.encode(contents, BarcodeFormat.QR_CODE, width, height, hints)
        return toBufferedImage(bitMatrix)
    }

    private fun toBufferedImage(matrix: BitMatrix): BufferedImage {
        val width: Int = matrix.getWidth()
        val height: Int = matrix.getHeight()
        val image = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
        val rowPixels = IntArray(width)
        var row = BitArray(width)
        for (y in 0 until height) {
            row = matrix.getRow(y, row)
            for (x in 0 until width) {
                rowPixels[x] = if (row.get(x)) BLACK else WHITE
            }
            image.setRGB(0, y, width, 1, rowPixels, 0, width)
        }
        return image
    }

    companion object {

        private const val WHITE = -0x1

        private const val BLACK = -0x1000000

    }
}