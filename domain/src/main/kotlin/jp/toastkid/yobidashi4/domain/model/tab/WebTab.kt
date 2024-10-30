package jp.toastkid.yobidashi4.domain.model.tab

import java.nio.file.Files
import java.util.UUID
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.domain.service.text.MarkdownLinkGenerator
import kotlin.io.path.pathString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class WebTab(
    private var title: String = "",
    private var url: String = "",
    private val id: String = UUID.randomUUID().toString()
) : Tab {

    override fun title(): String = title

    override fun closeable(): Boolean = true

    override fun iconPath(): String? {
        return makeIconPath()
    }

    private fun makeIconPath(): String? {
        if (url.isEmpty()) {
            return null
        }

        val faviconFolder = WebIcon()
        faviconFolder.makeFolderIfNeed()
        val iconPath = faviconFolder.find(url) ?: return null
        if (Files.exists(iconPath)) {
            return iconPath.pathString
        }
        return null
    }

    fun isReadableUrl() =
        url.startsWith("https://") || url.startsWith("http://")

    fun url() = url

    fun id() = id

    private val _updateFlow = MutableSharedFlow<Long>()

    override fun update(): Flow<Long> {
        return _updateFlow.asSharedFlow()
    }

    fun markdownLink() = MarkdownLinkGenerator().invoke(title, url)

}
