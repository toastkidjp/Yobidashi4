package jp.toastkid.yobidashi4.presentation.main.drop

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTargetDropEvent
import java.io.File
import java.io.IOException
import java.nio.file.Path
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.name
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory

class DropTarget : DragAndDropTarget, KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onDrop(event: DragAndDropEvent): Boolean {
        val dropTargetDropEvent = event.nativeEvent as? DropTargetDropEvent ?: return false

        try {
            if (dropTargetDropEvent.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dropTargetDropEvent.acceptDrop(DnDConstants.ACTION_COPY)
                val transferable = dropTargetDropEvent.transferable
                val list = transferable.getTransferData(
                    DataFlavor.javaFileListFlavor
                ) as List<*>
                val files = list.filterIsInstance<File>().map(File::toPath).sortedBy(::sortKey)
                mainViewModel.emitDroppedPath(files)
                dropTargetDropEvent.dropComplete(true)
                return true
            }
        } catch (ex: UnsupportedFlavorException) {
            LoggerFactory.getLogger(javaClass).warn("I/O error.", ex)
        } catch (ex: IOException) {
            LoggerFactory.getLogger(javaClass).warn("I/O error.", ex)
        }
        dropTargetDropEvent.rejectDrop()

        return false
    }

    private fun sortKey(path: Path) = path.name

}