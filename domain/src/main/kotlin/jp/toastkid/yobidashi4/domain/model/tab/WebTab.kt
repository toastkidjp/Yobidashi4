package jp.toastkid.yobidashi4.domain.model.tab

import java.util.UUID
import jp.toastkid.yobidashi4.domain.service.text.MarkdownLinkGenerator

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

    fun markdownLink() = MarkdownLinkGenerator().invoke(title, url)

}
