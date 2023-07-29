package jp.toastkid.yobidashi4.domain.model.tab

import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import java.util.stream.Collectors
import kotlin.io.path.pathString

data class WebTab(
    private val title: String = "",
    private val url: String = ""
) : Tab, Reloadable {

    private val id = UUID.randomUUID().toString()

    private val iconPathState = makeIconPath()

    override fun title(): String = title

    override fun closeable(): Boolean = true

    override fun iconPath(): String? {
        return iconPathState
    }

    private fun makeIconPath(): String? {
        if (url.isNullOrEmpty()) {
            return "images/icon/ic_web.xml"
        }

        val faviconFolder = Path.of("data/web/icon")
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

    override fun reload() {
        /*object : KoinComponent {
            val vm: WebTabViewModel by inject()
        }
            .vm.reload(id)*/
    }

}