package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import java.awt.Font
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory

class EditorAreaViewRefresherService : KoinComponent {

    private val setting: Setting by inject()

    operator fun invoke(editorArea: RSyntaxTextArea) {
        editorArea.lineWrap = setting.wrapLine()
        editorArea.foreground = setting.editorForegroundColor()
        editorArea.background = setting.editorBackgroundColor()
        val editorFontFamily = setting.editorFontFamily() ?: return
        val fontSize = setting.editorFontSize()
        val font = try {
            Font(editorFontFamily, editorArea.font.style, fontSize)
        } catch (e: Exception) {
            LoggerFactory.getLogger(javaClass).debug("Font finding error.", e)
            null
        } ?: return
        editorArea.font = font
    }

}