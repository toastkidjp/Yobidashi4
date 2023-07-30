package jp.toastkid.yobidashi4.domain.model.tab

import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import java.util.stream.Collectors
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

    private val _updateFlow = MutableSharedFlow<Long>()

    override fun update(): Flow<Long> {
        return _updateFlow.asSharedFlow()
    }

    override fun reload() {
        /*object : KoinComponent {
            val vm: WebTabViewModel by inject()
        }
            .vm.reload(id)*/
    }

    fun updateTitleAndUrl(title: String?, url: String?) {
        title?.let { this.title = title }
        url?.let { this.url = url }
        CoroutineScope(Dispatchers.Default).launch {
            _updateFlow.emit(System.currentTimeMillis())
        }
    }

}