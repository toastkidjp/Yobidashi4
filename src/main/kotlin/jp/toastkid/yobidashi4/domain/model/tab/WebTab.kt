package jp.toastkid.yobidashi4.domain.model.tab

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID
import java.util.stream.Collectors
import kotlin.io.path.pathString

class WebTab(
    private val title: MutableState<String> = mutableStateOf( ""),
    private var url: String = ""
) : Tab {

    private val id = UUID.randomUUID().toString()

    override fun title(): String = title.value

    override fun closeable(): Boolean = true

    override fun iconPath(): String? {
        if (url.isNullOrEmpty()) {
            return "images/icon/ic_web.xml"
        }

        val faviconFolder = Paths.get("data/web/icon")
        val iconPath = Files.list(faviconFolder).filter {
            val startsWith = it.fileName.pathString.startsWith(URL(url).host)
            startsWith
        }.collect(Collectors.toList()).firstOrNull() ?: return "images/icon/ic_web.xml"
        if (Files.exists(iconPath)) {
            return iconPath.pathString
        }
        return "images/icon/ic_web.xml"
    }

    fun isReadableUrl() =
        url.startsWith("https://") || url.startsWith("http://")

    fun url() = url

    fun id() = id

    fun updateTitle(newTitle: String) {
        if (title.value != newTitle) {
            this.title.value = newTitle
        }
    }

}