package jp.toastkid.yobidashi4.presentation.main.drop

import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTargetAdapter
import java.awt.dnd.DropTargetDropEvent
import java.io.File
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.name
import org.slf4j.LoggerFactory

class MainDropTargetListener(private val consumer: (List<Path>) -> Unit) : DropTargetAdapter() {

    override fun drop(dtde: DropTargetDropEvent?) {
        dtde ?: return
        try {
            if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY)
                val transferable = dtde.transferable
                val list = transferable.getTransferData(
                    DataFlavor.javaFileListFlavor
                ) as List<*>
                val files = list.filterIsInstance<File>().map(File::toPath).sortedBy(::sortKey)
                consumer(files)
                dtde.dropComplete(true)
                return
            }
        } catch (ex: UnsupportedFlavorException) {
            LoggerFactory.getLogger(javaClass).warn("I/O error.", ex)
        } catch (ex: IOException) {
            LoggerFactory.getLogger(javaClass).warn("I/O error.", ex)
        }
        dtde.rejectDrop()
    }

    private fun sortKey(path: Path) = path.name

}