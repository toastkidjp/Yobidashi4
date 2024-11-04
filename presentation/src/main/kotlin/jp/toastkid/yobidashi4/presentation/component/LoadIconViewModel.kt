package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.ui.graphics.ImageBitmap
import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_web
import jp.toastkid.yobidashi4.presentation.main.content.mapper.TabIconMapper
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap

class LoadIconViewModel {

    fun defaultIconPath() = Res.drawable.ic_web

    fun contentDescription(): String {
        return "Icon"
    }

    @OptIn(ExperimentalResourceApi::class)
    fun loadBitmap(url: String): ImageBitmap? {
        val faviconFolder = WebIcon()
        faviconFolder.makeFolderIfNeed()
        val iconPath = faviconFolder.find(url) ?: return null
        if (Files.exists(iconPath).not()) {
            return null
        }

        return Files.newInputStream(iconPath).use { inputStream ->
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