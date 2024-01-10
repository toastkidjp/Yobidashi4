package jp.toastkid.yobidashi4.presentation.main.drop

import java.awt.dnd.DropTarget
import java.nio.file.Path

class DropTargetFactory {

    operator fun invoke(consumer: (List<Path>) -> Unit): DropTarget {
        val dropTarget = DropTarget()
        dropTarget.addDropTargetListener(MainDropTargetListener(consumer))
        return dropTarget
    }

}