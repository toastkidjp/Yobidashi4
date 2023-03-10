package jp.toastkid.yobidashi4.infrastructure.model.browser

import java.awt.Component
import jp.toastkid.yobidashi4.domain.model.browser.BrowserPool
import jp.toastkid.yobidashi4.infrastructure.service.CefClientFactory
import org.cef.CefApp
import org.cef.CefClient
import org.cef.browser.CefBrowser
import org.koin.core.annotation.Single


@Single
class BrowserPoolImplementation : BrowserPool {

    private val client: CefClient

    private var lastId: String? = null

    init {
        client = CefClientFactory({ latestBrowser() }, { id -> browsers.entries.firstOrNull { it.value == id }?.key }).invoke()
    }

    override fun onLayout(x: Int, y: Int, width: Int, height: Int) {
        latestBrowser()?.uiComponent?.setBounds(x, y, width, height)
    }

    private fun latestBrowser(): CefBrowser? = browsers.get(lastId)

    private val browsers = mutableMapOf<String, CefBrowser>()

    override fun component(id: String, initialUrl: String): Component {
        val browser = getBrowser(id, initialUrl)
        lastId = id
        return browser.uiComponent
    }

    override fun devTools(id: String): Component {
        return getBrowser(id, "").devTools.uiComponent
    }

    override fun find(id: String, text: String, forward: Boolean) {
        getBrowser(id, "").find(text, forward, true, true)
    }

    private fun getBrowser(id: String, initialUrl: String): CefBrowser {
        val browser = browsers.getOrElse(id) { client.createBrowser(initialUrl, false, false) }
        browsers.put(id, browser)
        return browser
    }

    override fun reload(id: String) {
        getBrowser(id, "").reload()
    }

    override fun dispose(id: String) {
        browsers.get(id)?.close(true)
        browsers.remove(id)
    }

    override fun disposeAll() {
        browsers.keys.forEach {
            browsers.get(it)?.close(true)
        }
        browsers.clear()
        CefApp.getInstance().dispose()
    }

}