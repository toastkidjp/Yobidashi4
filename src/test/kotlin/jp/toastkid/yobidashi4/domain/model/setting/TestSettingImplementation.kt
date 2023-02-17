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

    override fun setUseInternalEditor(newValue: Boolean) {
        TODO("Not yet implemented")
    }

    override fun useInternalEditor(): Boolean {
        TODO("Not yet implemented")
    }

    override fun userOffDay(): List<Pair<Int, Int>> {
        TODO("Not yet implemented")
    }

    override fun setUseCaseSensitiveInFinder(use: Boolean) {
        TODO("Not yet implemented")
    }

    override fun useCaseSensitiveInFinder(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setEditorBackgroundColor(color: Color?) {
        TODO("Not yet implemented")
    }

    override fun editorBackgroundColor(): Color? {
        TODO("Not yet implemented")
    }

    override fun setEditorForegroundColor(color: Color?) {
        TODO("Not yet implemented")
    }

    override fun editorForegroundColor(): Color? {
        TODO("Not yet implemented")
    }

    override fun resetEditorColorSetting() {
        TODO("Not yet implemented")
    }

    override fun setEditorFontFamily(fontFamily: String?) {
        TODO("Not yet implemented")
    }

    override fun editorFontFamily(): String? {
        TODO("Not yet implemented")
    }

    override fun setEditorFontSize(size: Int?) {
        TODO("Not yet implemented")
    }

    override fun editorFontSize(): Int {
        TODO("Not yet implemented")
    }

    override fun mediaPlayerPath(): String? {
        TODO("Not yet implemented")
    }

    override fun mediaFolderPath(): String? {
        TODO("Not yet implemented")
    }

    override fun privateSearchPath(): String? {
        TODO("Not yet implemented")
    }

    override fun privateSearchOption(): String? {
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

    override fun save() {
        TODO("Not yet implemented")
    }
}