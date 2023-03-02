package jp.toastkid.yobidashi4.domain.service.tool.rename

import java.awt.Dimension
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetAdapter
import java.awt.dnd.DropTargetDropEvent
import java.awt.event.ActionEvent
import java.io.File
import java.io.IOException
import java.nio.file.Files
import javax.swing.AbstractAction
import javax.swing.BoxLayout
import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JOptionPane
import javax.swing.JPanel
import kotlin.io.path.extension
import org.slf4j.LoggerFactory

class FileRenameService {

    operator fun invoke() {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.PAGE_AXIS)
        panel.add(JLabel("Please would you drop any file which you want to rename?"))
        val droppedFileList = JList<File>()
        droppedFileList.preferredSize = Dimension(300, 400)
        panel.add(droppedFileList)
        val defaultListModel = DefaultListModel<File>()
        droppedFileList.model = defaultListModel
        val dropTarget = DropTarget()
        dropTarget.addDropTargetListener(
            object : DropTargetAdapter() {
                override fun drop(dtde: DropTargetDropEvent?) {
                    dtde ?: return
                    try {
                        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            dtde.acceptDrop(DnDConstants.ACTION_COPY)
                            val transferable = dtde.transferable
                            val list = transferable.getTransferData(
                                DataFlavor.javaFileListFlavor
                            ) as List<*>
                            list.filterIsInstance<File>().forEach { defaultListModel.addElement(it) }
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
            }
        )
        panel.dropTarget = dropTarget
        panel.add(JButton().also {
            it.action = object : AbstractAction() {
                override fun actionPerformed(e: ActionEvent?) {
                    defaultListModel.clear()
                }
            }
            it.text = "Clear"
        })
        panel.add(JLabel("Base file name"))
        val input = JOptionPane.showInputDialog(null, panel, "Files rename", JOptionPane.QUESTION_MESSAGE)
        if (input.isNullOrBlank() || defaultListModel.isEmpty) {
            return
        }
        defaultListModel.elements().toList().map { it.toPath() }.forEachIndexed { i, p ->
            Files.copy(p, p.resolveSibling("${input}_${i + 1}.${p.extension}"))
        }
    }

}