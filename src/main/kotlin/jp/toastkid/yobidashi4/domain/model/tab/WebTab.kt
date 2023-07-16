package jp.toastkid.yobidashi4.domain.model.tab

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import java.util.stream.Collectors
import jp.toastkid.yobidashi4.presentation.viewmodel.web.WebTabViewModel
import kotlin.io.path.pathString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WebTab(
    private val title: MutableState<String> = mutableStateOf( ""),
    private var url: String = ""
) : Tab, Reloadable {

    private val id = UUID.randomUUID().toString()

    private val iconPathState = mutableStateOf(makeIconPath())

    override fun title(): String = title.value

    override fun closeable(): Boolean = true

    override fun iconPath(): String? {
        return iconPathState.value
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

    fun update(newTitle: String, newUrl: String?) {
        if (title.value != newTitle) {
            this.title.value = newTitle
        }
        if (newUrl != null && url != newUrl) {
            this.url = newUrl
            iconPathState.value = makeIconPath()
        }
    }

    override fun reload() {
        object : KoinComponent {
            val vm: WebTabViewModel by inject()
        }
            .vm.reload(id)
    }

}