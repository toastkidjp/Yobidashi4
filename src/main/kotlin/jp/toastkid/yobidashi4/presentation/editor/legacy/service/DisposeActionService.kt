package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import javax.swing.JFrame
import javax.swing.JOptionPane

class DisposeActionService(private val frame: JFrame) {

    operator fun invoke(shouldNotShowIndicator: Boolean) {
        if (shouldNotShowIndicator) {
            frame.dispose()
            return
        }

        val choice = JOptionPane.showConfirmDialog(
            frame,
            "Would you like to close this editor window? This file is editing."
        )

        if (choice == JOptionPane.OK_OPTION) {
            frame.dispose()
        }
    }

}