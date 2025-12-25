/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
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