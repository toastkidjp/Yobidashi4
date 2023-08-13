package jp.toastkid.yobidashi4.domain.service.barcode

import java.awt.image.BufferedImage

interface BarcodeEncoder {

    operator fun invoke(contents: String?, width: Int, height: Int): BufferedImage?

}