package jp.toastkid.yobidashi4.presentation.editor.setting

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.godaddy.android.colorpicker.HsvColor
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EditorSettingViewModel : KoinComponent {

    private val setting: Setting by inject()

    private val currentBackgroundColor = mutableStateOf(
        setting.editorBackgroundColor()?.let {
            Color(it.red, it.green, it.blue, it.alpha)
        } ?: Color.LightGray
    )

    private val currentFontColor =  mutableStateOf(
        setting.editorForegroundColor()?.let {
            Color(it.red, it.green, it.blue, it.alpha)
        } ?: Color.Black
    )

    fun currentBackgroundColor() = currentBackgroundColor.value

    fun currentFontColor() = currentFontColor.value

    fun onBackgroundColorChanged(hsvColor: HsvColor) {
        currentBackgroundColor.value = hsvColor.toColor()
    }

    fun onFontColorChanged(hsvColor: HsvColor) {
        currentFontColor.value = hsvColor.toColor()
    }

    fun commit() {
        setting.setEditorBackgroundColor(
            java.awt.Color(
                currentBackgroundColor.value.red,
                currentBackgroundColor.value.green,
                currentBackgroundColor.value.blue,
                currentBackgroundColor.value.alpha
            )
        )
        setting.setEditorForegroundColor(
            java.awt.Color(
                currentFontColor.value.red,
                currentFontColor.value.green,
                currentFontColor.value.blue,
                currentFontColor.value.alpha
            )
        )
    }

    fun editorFontFamily(): String? {
        return setting.editorFontFamily()
    }

    fun setEditorFontFamily(toString: String) {
        setting.setEditorFontFamily(toString)
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