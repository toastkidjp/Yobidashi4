package jp.toastkid.yobidashi4.presentation.editor.legacy.service.factory

import javax.swing.JComboBox
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FontSizeSpinnerFactory() : KoinComponent {

    private val setting: Setting by inject()

    operator fun invoke(applyColorSetting: () -> Unit): JComboBox<Int> {
        val sizeSpinner = JComboBox<Int>()
        (9..20).forEach {
            sizeSpinner.addItem(it)
        }
        sizeSpinner.addItemListener {
            setting.setEditorFontSize(Integer.parseInt(it.item?.toString()))
            applyColorSetting()
        }
        sizeSpinner.selectedItem = setting.editorFontSize()
        return sizeSpinner
    }

}