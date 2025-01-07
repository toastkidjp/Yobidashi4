package jp.toastkid.yobidashi4.infrastructure.model.browser

import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.infrastructure.service.web.CefClientFactory
import org.cef.CefApp
import org.cef.CefClient
import org.cef.browser.CefBrowser
import org.koin.core.annotation.Single
import java.awt.Component
import java.util.concurrent.atomic.AtomicReference
import javax.swing.JDialog
import javax.swing.WindowConstants

@Single
class WebViewPoolImplementation : WebViewPool {

    private val cefClientFactory = CefClientFactory()

    private val client: CefClient = cefClientFactory.invoke()

    private val browsers = mutableMapOf<String, CefBrowser>()

    private val lastId = AtomicReference("")

    override fun component(id: String, initialUrl: String): Component {
        val browser = getBrowser(id, initialUrl)
        return browser.uiComponent
    }

    override fun devTools(id: String): Component {
        return getBrowser(id, "").devTools.uiComponent
    }

    override fun find(id: String, text: String, forward: Boolean) {
        getBrowser(id, "").find(text, forward, true, true)
    }

    override fun clearFind(id: String) {
        getBrowser(id, "").stopFinding(true)
    }

    private fun getBrowser(id: String, initialUrl: String): CefBrowser {
        browsers.get(lastId.get())?.stopLoad()

        val browser = browsers.getOrElse(id) { client.createBrowser(initialUrl, false, false) }
        browsers.put(id, browser)
        return browser
    }

    override fun reload(id: String) {
        getBrowser(id, "").reload()
    }

    override fun switchDevTools(id: String) {
        val devToolsDialog = JDialog()
        devToolsDialog.defaultCloseOperation = WindowConstants.HIDE_ON_CLOSE
        devToolsDialog.setSize(800, 600)
        devToolsDialog.add(devTools(id))
        devToolsDialog.isVisible = true
    }

    override fun dispose(id: String) {
        val browser = browsers.get(id) ?: return
        browser.close(true)
        client.doClose(browser)
        browsers.remove(id)
    }

    override fun disposeAll() {
        if (CefApp.getState() == CefApp.CefAppState.NONE) {
            return
        }

        ArrayList(browsers.keys).forEach { dispose(it) }
        client.dispose()
        CefApp.getInstance().dispose()
    }

    override fun findId(browser: Any): String? {
        if (browser !is CefBrowser) {
            return null
        }

        return browsers.entries.firstOrNull { it.value == browser }?.key
    }

}