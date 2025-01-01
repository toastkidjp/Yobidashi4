package jp.toastkid.yobidashi4.presentation.editor.setting

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.github.skydoves.colorpicker.compose.ColorPickerController
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EditorSettingViewModel : KoinComponent {

    private val setting: Setting by inject()

    private val currentBackgroundColor = mutableStateOf(
        ColorPickerController().also {
            it.selectByColor(
                setting.editorBackgroundColor()?.let {
                    Color(it.red, it.green, it.blue, it.alpha)
                } ?: Color.LightGray,
                false
            )
        }
    )

    private val currentFontColor =  mutableStateOf(
        ColorPickerController().also {
            it.selectByColor(
                setting.editorForegroundColor()?.let {
                    Color(it.red, it.green, it.blue, it.alpha)
                } ?: Color.Black,
                false
            )
        }

    )

    fun currentBackgroundColor() = currentBackgroundColor.value

    fun currentFontColor() = currentFontColor.value

    fun onBackgroundColorChanged(hsvColor: Color) {
        //currentBackgroundColor.value = hsvColor
    }

    fun onFontColorChanged(hsvColor: Color) {
        //currentFontColor.value = hsvColor.toArgb()
    }

    fun commit() {
        setting.setEditorBackgroundColor(
            java.awt.Color(
                currentBackgroundColor.value.selectedColor.value.red,
                currentBackgroundColor.value.selectedColor.value.green,
                currentBackgroundColor.value.selectedColor.value.blue,
                currentBackgroundColor.value.selectedColor.value.alpha
            )
        )
        setting.setEditorForegroundColor(
            java.awt.Color(
                currentFontColor.value.selectedColor.value.red,
                currentFontColor.value.selectedColor.value.green,
                currentFontColor.value.selectedColor.value.blue,
                currentFontColor.value.selectedColor.value.alpha
            )
        )
    }

    private val openFontFamily = mutableStateOf(false)

    fun isOpenFontFamily() = openFontFamily.value

    fun openFontFamily() {
        openFontFamily.value = true
    }

    fun closeFontFamily() {
        openFontFamily.value = false
    }

    fun editorFontFamily(): String? {
        return setting.editorFontFamily()
    }

    fun setEditorFontFamily(toString: String) {
        setting.setEditorFontFamily(toString)
    }

    private val openFontSize = mutableStateOf(false)

    fun isOpenFontSize() = openFontSize.value

    fun openFontSize() {
        openFontSize.value = true
    }

    fun closeFontSize() {
        openFontSize.value = false
    }

    fun editorFontSize(): Int {
        return setting.editorFontSize()
    }

    fun setEditorFontSize(it: Any) {
        if (it is Int) {
            setting.setEditorFontSize(it)
        }
    }

    fun reset() {
        setting.resetEditorColorSetting()
    }

}