package jp.toastkid.yobidashi4.domain.model.browser

import java.awt.Component
import jp.toastkid.yobidashi4.domain.service.web.event.WebTabEvent
import kotlinx.coroutines.flow.SharedFlow

interface WebViewPool {
    fun component(id: String, initialUrl: String): Component
    fun devTools(id: String): Component
    fun dispose(id: String)
    fun disposeAll()
    fun find(id: String, text: String, forward: Boolean)

    fun clearFind(id: String)

    fun reload(id: String)

    fun event(): SharedFlow<WebTabEvent>

    fun switchDevTools(id: String)

    fun findId(browser: Any): String?

}