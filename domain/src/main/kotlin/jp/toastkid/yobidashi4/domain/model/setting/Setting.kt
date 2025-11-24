package jp.toastkid.yobidashi4.domain.model.setting

import java.awt.Color
import java.nio.file.Path

interface Setting {

    fun darkMode(): Boolean

    fun setDarkMode(newValue: Boolean)

    fun articleFolder(): String

    fun articleFolderPath(): Path

    fun setArticleFolderPath(path: String)

    /*
    fun sorting(): String

    fun setSorting(newValue: Sorting)
*/

    fun userOffDay(): List<Pair<Int, Int>>

    fun setUseCaseSensitiveInFinder(use: Boolean)

    fun useCaseSensitiveInFinder(): Boolean

    fun setEditorBackgroundColor(color: Color?)

    fun editorBackgroundColor(): Color?

    fun setEditorForegroundColor(color: Color?)

    fun editorForegroundColor(): Color?

    fun resetEditorColorSetting()

    fun setEditorFontFamily(fontFamily: String?)

    fun editorFontFamily(): String?

    fun setEditorFontSize(size: Int?)

    fun editorFontSize(): Int

    fun editorLineHeight(): Float

    fun editorConversionLimit(): Int

    fun mediaPlayerPath(): String?

    fun mediaFolderPath(): String?

    fun wrapLine(): Boolean

    fun switchWrapLine()

    fun getMaskingCount(): Int

    fun setMaskingCount(it: Int)

    fun userAgentName(): String

    fun setUserAgentName(newValue: String)

    fun useBackground(): Boolean

    fun switchUseBackground()

    fun chatApiKey(): String?

    fun save()

    fun items(): Map<Any, Any>

    fun update(key: String, text: String)

}