package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import java.awt.Color
import javax.swing.JColorChooser
import javax.swing.JOptionPane

class ColorChooserService {

    operator fun invoke(): Color? {
        val colorPicker = JColorChooser()
        val dialog = JOptionPane.showConfirmDialog(
                null,
                colorPicker
        )
        if (dialog != JOptionPane.OK_OPTION) {
            return null
        }
        return colorPicker.color
    }

}