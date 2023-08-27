package jp.toastkid.yobidashi4.domain.model.clipboard

import java.awt.Image
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.IOException

class TransferableImage(private val i: Image) : Transferable {

    @Throws(UnsupportedFlavorException::class, IOException::class)
    override fun getTransferData(flavor: DataFlavor): Any {
        return if (flavor.equals(DataFlavor.imageFlavor)) {
            i
        } else {
            throw UnsupportedFlavorException(flavor)
        }
    }

    override fun getTransferDataFlavors(): Array<DataFlavor> {
        return arrayOf( DataFlavor.imageFlavor)
    }

    override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
        return flavor == DataFlavor.imageFlavor
    }
}