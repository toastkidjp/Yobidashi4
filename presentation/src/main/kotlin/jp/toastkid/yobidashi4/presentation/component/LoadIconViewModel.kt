package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.ui.graphics.ImageBitmap
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_web
import jp.toastkid.yobidashi4.presentation.main.content.mapper.TabIconMapper
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap

class LoadIconViewModel {

    fun useIcon(pathString: String?): Boolean {
        return pathString != null && pathString.contains("images/icon/")
    }

    fun defaultIconPath() = Res.drawable.ic_web

    fun contentDescription(): String {
        return "Icon"
    }

    @OptIn(ExperimentalResourceApi::class)
    fun loadBitmap(pathString: String?): ImageBitmap? {
        val path = Path.of(pathString)
        if (pathString == null || Files.exists(path).not()) {
            return null
        }

        return Files.newInputStream(path).use { inputStream ->
            try {
                inputStream.readAllBytes().decodeToImageBitmap()
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    private val tabIconMapper = TabIconMapper()

    fun loadTabIcon(tab: Tab): DrawableResource? {
        return tabIconMapper(tab)
    }

}