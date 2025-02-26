package jp.toastkid.yobidashi4.presentation.editor.setting

import androidx.compose.runtime.mutableStateOf
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EditorSettingViewModel : KoinComponent {

    private val setting: Setting by inject()

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