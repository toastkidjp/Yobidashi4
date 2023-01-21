package jp.toastkid.yobidashi4.domain.model.tab

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.util.UUID

class WebTab(
    private val title: MutableState<String> = mutableStateOf( ""),
    private var url: String = ""
) : Tab {

    private val id = UUID.randomUUID().toString()

    override fun title(): String = title.value

    override fun closeable(): Boolean = true

    override fun iconPath(): String? {
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