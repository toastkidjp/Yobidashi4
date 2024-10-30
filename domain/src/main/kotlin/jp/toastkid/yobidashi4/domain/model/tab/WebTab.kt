package jp.toastkid.yobidashi4.domain.model.tab

import java.util.UUID
import jp.toastkid.yobidashi4.domain.service.text.MarkdownLinkGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class WebTab(
    private val title: String = "",
    private val url: String = "",
    private val id: String = UUID.randomUUID().toString()
) : Tab {

    override fun title(): String = title

    override fun closeable(): Boolean = true

    override fun iconPath(): String? {
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
