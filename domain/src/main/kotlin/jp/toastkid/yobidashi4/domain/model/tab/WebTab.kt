package jp.toastkid.yobidashi4.domain.model.tab

import java.nio.file.Files
import java.util.UUID
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.domain.service.text.MarkdownLinkGenerator
import kotlin.io.path.pathString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class WebTab(
    private var title: String = "",
    private var url: String = ""
) : Tab {

    private val id = UUID.randomUUID().toString()

    override fun title(): String = title

    override fun closeable(): Boolean = true

    override fun iconPath(): String? {
        return makeIconPath()
    }

    private fun makeIconPath(): String? {
        if (url.isEmpty()) {
            return DEFAULT_ICON_PATH
        }

        val faviconFolder = WebIcon()
        faviconFolder.makeFolderIfNeed()
        val iconPath = faviconFolder.find(url) ?: return DEFAULT_ICON_PATH
        if (Files.exists(iconPath)) {
            return iconPath.pathString
        }
        return DEFAULT_ICON_PATH
    }

    fun isReadableUrl() =
        url.startsWith("https://") || url.startsWith("http://")

    fun url() = url

    fun id() = id

    private val _updateFlow = MutableSharedFlow<Long>()

    override fun update(): Flow<Long> {
        return _updateFlow.asSharedFlow()
    }

    fun updateTitleAndUrl(title: String?, url: String?) {
        title?.let { this.title = title }
        url?.let { this.url = url }
        CoroutineScope(Dispatchers.Default).launch {
            _updateFlow.emit(System.currentTimeMillis())
        }
    }

    fun markdownLink() = MarkdownLinkGenerator().invoke(title, url)

}

private const val DEFAULT_ICON_PATH = "images/icon/ic_web.xml"
