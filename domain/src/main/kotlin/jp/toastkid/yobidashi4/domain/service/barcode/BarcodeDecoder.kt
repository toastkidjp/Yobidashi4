package jp.toastkid.yobidashi4.domain.service.barcode

import java.awt.image.BufferedImage

interface BarcodeDecoder {

    operator fun invoke(sourceImage: BufferedImage): String?

}