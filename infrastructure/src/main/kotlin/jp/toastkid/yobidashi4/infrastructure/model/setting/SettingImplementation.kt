package jp.toastkid.yobidashi4.infrastructure.model.setting

import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.presentation.editor.setting.ColorDecoderService
import org.koin.core.annotation.Single
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties
import kotlin.io.path.exists

@Single
class SettingImplementation : Setting {

    private val properties = Properties()

    init {
        val path = Path.of(PATH)
        if (Files.exists(path.parent).not()) {
            Files.createDirectories(path.parent)
        }
        if (Files.exists(path).not()) {
            Files.createFile(path)
        }

        properties.load(Files.newBufferedReader(path))
    }

    override fun darkMode(): Boolean {
        return properties.getProperty("use_dark_mode")?.toBoolean() ?: false
    }

    override fun setDarkMode(newValue: Boolean) {
        properties.setProperty("use_dark_mode", newValue.toString())
    }

    override fun  articleFolder(): String = properties.getProperty("article.folder") ?: ""

    override fun  articleFolderPath() = Path.of(articleFolder())

    override fun setArticleFolderPath(path: String) {
        properties.setProperty("article.folder", path)
    }

    override fun  userOffDay(): List<Pair<Int, Int>> {
        val offDayString = properties.getProperty("user_off_day") ?: return emptyList()
        return offDayString.split(",")
            .filter { it.contains("/") }
            .map {
                it.split("/")
                    .let { monthAndDate -> monthAndDate[0].toInt() to monthAndDate[1].toInt() }
            }
    }

    override fun  setUseCaseSensitiveInFinder(use: Boolean) {
        properties.setProperty("use_case_sensitive", use.toString())
    }

    override fun  useCaseSensitiveInFinder(): Boolean {
        return properties.getProperty("use_case_sensitive")?.toBoolean() ?: false
    }

    override fun  setEditorBackgroundColor(color: java.awt.Color?) {
        properties.setProperty("editor_background_color", Integer.toHexString((color ?: java.awt.Color.WHITE).rgb))
    }

    override fun  editorBackgroundColor(): java.awt.Color {
        return java.awt.Color(225, 225, 225, 255)
    }

    override fun  setEditorForegroundColor(color: java.awt.Color?) {
        properties.setProperty("editor_foreground_color", Integer.toHexString((color ?: java.awt.Color.BLACK).rgb))
    }

    override fun  editorForegroundColor(): java.awt.Color? {
        return ColorDecoderService().invoke(properties.getProperty("editor_foreground_color"))
    }

    override fun  resetEditorColorSetting() {
        properties.remove("editor_foreground_color")
        properties.remove("editor_background_color")
    }

    override fun setEditorFontFamily(fontFamily: String?) {
        fontFamily?.let {
            properties.setProperty("editor_font_family", it)
        }
    }

    override fun editorFontFamily(): String? {
        return properties.getProperty("editor_font_family")
    }

    override fun setEditorFontSize(size: Int?) {
        size?.let {
            properties.setProperty("editor_font_size", size.toString())
        }
    }

    override fun editorFontSize(): Int {
        return properties.getProperty("editor_font_size")?.toIntOrNull() ?: 14
    }

    override fun editorConversionLimit(): Int {
        return properties.getProperty("editor_conversion_limit")?.toIntOrNull() ?: 4_500
    }

    override fun mediaPlayerPath() = properties.getProperty("media_player_path")

    override fun mediaFolderPath() = properties.getProperty("media_folder_path")

    override fun save() {
        val path = Path.of(PATH)
        if (path.parent.exists().not()) {
            Files.createDirectory(path.parent)
        }
        properties.store(Files.newBufferedWriter(path), null)
    }

    override fun wrapLine() = properties.getProperty("editor_wrap_line").toBoolean()

    override fun switchWrapLine() {
        properties.setProperty("editor_wrap_line", wrapLine().not().toString())
    }

    override fun getMaskingCount(): Int =
        properties.getProperty("number_place_masking_count")?.toIntOrNull() ?: 20

    override fun setMaskingCount(it: Int) {
        properties.setProperty("number_place_masking_count", "$it")
    }

    override fun userAgentName(): String =
        properties.getProperty("user_agent_name") ?: ""

    override fun setUserAgentName(newValue: String) {
        properties.setProperty("user_agent_name", newValue)
    }

    override fun useBackground(): Boolean =
        properties.getProperty("use_background")?.toBooleanStrictOrNull() ?: true

    override fun switchUseBackground() {
        properties.setProperty("use_background", useBackground().not().toString())
    }

    override fun chatApiKey(): String? {
        return properties.getProperty("chat_api_key")?.toString()
    }

}

private const val PATH = "user/setting.properties"
