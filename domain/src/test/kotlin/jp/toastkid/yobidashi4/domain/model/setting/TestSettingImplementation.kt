package jp.toastkid.yobidashi4.domain.model.setting

import io.mockk.mockk
import java.awt.Color
import java.nio.file.Path

class TestSettingImplementation : Setting {
    override fun darkMode(): Boolean = false

    override fun setDarkMode(newValue: Boolean) {

    }

    override fun articleFolder(): String {
        return ""
    }

    override fun articleFolderPath(): Path {
        return mockk()
    }

    override fun userOffDay(): List<Pair<Int, Int>> {
        return listOf(12 to 29)
    }

    override fun setUseCaseSensitiveInFinder(use: Boolean) {
        TODO("Not yet implemented")
    }

    override fun useCaseSensitiveInFinder(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setEditorBackgroundColor(color: Color?) {
        // NOOP.
    }

    override fun editorBackgroundColor(): Color? {
        return Color.WHITE
    }

    override fun setEditorForegroundColor(color: Color?) {
        // NOOP.
    }

    override fun editorForegroundColor(): Color? {
        return Color.BLACK
    }

    override fun resetEditorColorSetting() {
        // NOOP.
    }

    override fun setEditorFontFamily(fontFamily: String?) {
        // NOOP.
    }

    override fun editorFontFamily(): String? {
        return null
    }

    override fun setEditorFontSize(size: Int?) {
        // NOOP.
    }

    override fun editorFontSize(): Int {
        return 16
    }

    override fun mediaPlayerPath(): String? {
        TODO("Not yet implemented")
    }

    override fun mediaFolderPath(): String? {
        TODO("Not yet implemented")
    }

    override fun wrapLine(): Boolean {
        TODO("Not yet implemented")
    }

    override fun switchWrapLine() {
        TODO("Not yet implemented")
    }

    override fun getMaskingCount(): Int {
        TODO("Not yet implemented")
    }

    override fun setMaskingCount(it: Int) {
        TODO("Not yet implemented")
    }

    override fun userAgentName(): String {
        TODO("Not yet implemented")
    }

    override fun setUserAgentName(newValue: String) {
        TODO("Not yet implemented")
    }

    override fun useBackground(): Boolean {
        TODO("Not yet implemented")
    }

    override fun switchUseBackground() {
        TODO("Not yet implemented")
    }

    override fun save() {
        TODO("Not yet implemented")
    }

    override fun chatApiKey(): String? {
        TODO("Not yet implemented")
    }
}