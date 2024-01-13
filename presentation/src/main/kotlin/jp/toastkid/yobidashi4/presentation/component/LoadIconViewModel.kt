package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import java.nio.file.Files
import java.nio.file.Path

class LoadIconViewModel {

    fun useIcon(pathString: String?): Boolean {
        return pathString != null && pathString.contains("images/icon/")
    }

    fun defaultIconPath(): String {
        return "images/icon/ic_web.xml"
    }

    fun contentDescription(): String {
        return "Icon"
    }

    fun loadBitmap(pathString: String?): ImageBitmap? {
        val path = Path.of(pathString)
        if (pathString == null || Files.exists(path).not()) {
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