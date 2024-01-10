package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import java.nio.file.Files
import java.nio.file.Path

class LoadIconViewModel(private val iconPath: String) {

    private val path = if (iconPath.isBlank()) null else Path.of(iconPath)

    fun useIcon() = iconPath.contains("temporary").not()

    fun defaultIconPath(): String {
        return "images/icon/ic_web.xml"
    }

    fun contentDescription(): String {
        return "Icon"
    }

    fun loadBitmap(): ImageBitmap? {
        if (path == null || Files.exists(path).not()) {
            return null
        }

        return Files.newInputStream(path).use { inputStream ->
            try {
                loadImageBitmap(inputStream)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

}