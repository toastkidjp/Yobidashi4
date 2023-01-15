package jp.toastkid.yobidashi4.infrastructure.model.browser

import java.awt.Component
import java.awt.Desktop
import java.awt.Point
import java.awt.Robot
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Locale
import java.util.UUID
import javax.imageio.ImageIO
import javax.swing.JDialog
import javax.swing.SwingUtilities
import javax.swing.WindowConstants
import jp.toastkid.yobidashi4.domain.model.browser.BrowserPool
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.model.web.user_agent.UserAgent
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import jp.toastkid.yobidashi4.domain.service.web.UrlOpenerService
import jp.toastkid.yobidashi4.presentation.editor.legacy.service.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import jp.toastkid.yobidashi4.presentation.viewmodel.web.WebTabViewModel
import kotlin.io.path.absolutePathString
import me.friwi.jcefmaven.CefAppBuilder
import me.friwi.jcefmaven.impl.progress.ConsoleProgressHandler
import org.cef.CefApp
import org.cef.CefClient
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.callback.CefBeforeDownloadCallback
import org.cef.callback.CefContextMenuParams
import org.cef.callback.CefDownloadItem
import org.cef.callback.CefMenuModel
import org.cef.handler.CefAppHandlerAdapter
import org.cef.handler.CefContextMenuHandlerAdapter
import org.cef.handler.CefDisplayHandlerAdapter
import org.cef.handler.CefDownloadHandlerAdapter
import org.cef.handler.CefKeyboardHandler.CefKeyEvent
import org.cef.handler.CefKeyboardHandlerAdapter
import org.cef.handler.CefLifeSpanHandlerAdapter
import org.cef.handler.CefLoadHandlerAdapter
import org.cef.handler.CefRequestHandlerAdapter
import org.cef.handler.CefResourceRequestHandler
import org.cef.handler.CefResourceRequestHandlerAdapter
import org.cef.misc.BoolRef
import org.cef.misc.EventFlags
import org.cef.network.CefRequest
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


@Single
class BrowserPoolImplementation : BrowserPool {

    private val client: CefClient

    private var lastId: String? = null

    init {
        val appSetting = object : KoinComponent { val s: Setting by inject() }.s

        val builder = CefAppBuilder()
        builder.setInstallDir(File("jcef-bundle")) //Default
        builder.setProgressHandler(ConsoleProgressHandler()) //Default
        CefApp.addAppHandler(object : CefAppHandlerAdapter(arrayOf(
            "--disable-gpu", "--force-fieldtrial-params", "--enable-features=WebContentsForceDark")) {})

        val settings = builder.cefSettings
        settings.windowless_rendering_enabled = false //Default - select OSR mode
        settings.background_color = settings.ColorType(80, 0, 0, 0)
        settings.user_agent = UserAgent.findByName(appSetting.userAgentName()).text()
        settings.locale = Locale.getDefault().language

        val adHosts = javaClass.classLoader.getResourceAsStream("web/ad_hosts.txt")?.use { stream ->
            return@use BufferedReader(InputStreamReader(stream)).use { reader ->
                reader.lines().toList()
            }
        } ?: emptyList()

        var selectedText = ""

        val cefApp = builder.build()
        client = cefApp.createClient()
        client.addLoadHandler(object : CefLoadHandlerAdapter() {
            // TODO Impl load action
        })

        client.addLifeSpanHandler(object : CefLifeSpanHandlerAdapter() {
            override fun onBeforePopup(
                browser: CefBrowser?,
                frame: CefFrame?,
                target_url: String?,
                target_frame_name: String?
            ): Boolean {
                target_url ?: return true
                MainViewModel.get().openUrl(target_url, false)
                return true
            }
        })
        client.addRequestHandler(object : CefRequestHandlerAdapter() {

            override fun getResourceRequestHandler(
                browser: CefBrowser?,
                frame: CefFrame?,
                request: CefRequest?,
                isNavigation: Boolean,
                isDownload: Boolean,
                requestInitiator: String?,
                disableDefaultHandling: BoolRef?
            ): CefResourceRequestHandler {
                return object : CefResourceRequestHandlerAdapter() {
                    override fun onBeforeResourceLoad(browser: CefBrowser?, frame: CefFrame?, request: CefRequest?): Boolean {
                        if (adHosts.any { request?.url?.contains(it) == true }) {
                            request?.dispose()
                            return true
                        }
                        return super.onBeforeResourceLoad(browser, frame, request)
                    }
                }
            }

            override fun onOpenURLFromTab(
                browser: CefBrowser?,
                frame: CefFrame?,
                target_url: String?,
                user_gesture: Boolean
            ): Boolean {
                if (user_gesture) {
                    target_url?.let {
                        object : KoinComponent{ val viewModel: MainViewModel by inject() }.viewModel
                            .openUrl(it, true)
                    }
                    return true
                }
                return super.onOpenURLFromTab(browser, frame, target_url, user_gesture)
            }
        })
        client.addDisplayHandler(object : CefDisplayHandlerAdapter() {
            override fun onTitleChange(browser: CefBrowser?, title: String?) {
                title ?: return
                val id = browsers.entries.firstOrNull { it.value == browser }?.key ?: return
                MainViewModel.get().updateWebTab(id, title)
            }
        })
        client.addDownloadHandler(object : CefDownloadHandlerAdapter() {
            override fun onBeforeDownload(
                browser: CefBrowser?,
                downloadItem: CefDownloadItem?,
                suggestedName: String?,
                callback: CefBeforeDownloadCallback?
            ) {
                val downloadFolder = Paths.get("user/download")
                if (Files.exists(downloadFolder).not()) {
                    Files.createDirectories(downloadFolder)
                }
                callback?.Continue(downloadFolder.resolve(suggestedName).absolutePathString(), false)
            }
        })

        client.addKeyboardHandler(object : CefKeyboardHandlerAdapter() {
            override fun onKeyEvent(browser: CefBrowser?, event: CefKeyEvent?): Boolean {
                event ?: return false
                if (event.type != CefKeyEvent.EventType.KEYEVENT_KEYUP) {
                    return false
                }

                if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == KeyEvent.VK_F) {
                    object : KoinComponent{ val viewModel: WebTabViewModel by inject() }.viewModel.switchFind()
                    return true
                }
                if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == KeyEvent.VK_W) {
                    object : KoinComponent{ val viewModel: MainViewModel by inject() }.viewModel.closeCurrent()
                    return true
                }
                if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == KeyEvent.VK_P) {
                    lastId?.let {
                        printPdf(it)
                    }
                    return true
                }
                if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == KeyEvent.VK_UP) {
                    latestBrowser()?.executeJavaScript("window.scrollTo(0, 0);", null, 1)
                    return true
                }
                if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == KeyEvent.VK_DOWN) {
                    latestBrowser()?.executeJavaScript("window.scrollTo(0, document.body.scrollHeight);", null, 1)
                    return true
                }
                if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == KeyEvent.VK_B) {
                    addBookmark()
                    return true
                }
                if (event.windows_key_code == KeyEvent.VK_F5) {
                    latestBrowser()?.reload()
                    return true
                }
                if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == KeyEvent.VK_R) {
                    latestBrowser()?.reloadIgnoreCache()
                    return true
                }
                if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == 187) {
                    latestBrowser()?.let {
                        it.zoomLevel = it.zoomLevel + 0.25
                    }
                    return true
                }
                if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == 189) {
                    latestBrowser()?.let {
                        it.zoomLevel = it.zoomLevel - 0.25
                    }
                    return true
                }
                if (event.modifiers == EventFlags.EVENTFLAG_ALT_DOWN && event.windows_key_code == KeyEvent.VK_LEFT) {
                    latestBrowser()?.let {
                        if (it.canGoBack()) {
                            it.goBack()
                        }
                    }
                    return true
                }
                if (event.modifiers == EventFlags.EVENTFLAG_ALT_DOWN && event.windows_key_code == KeyEvent.VK_RIGHT) {
                    latestBrowser()?.let {
                        if (it.canGoForward()) {
                            it.goForward()
                        }
                    }
                    return true
                }
                if (event.modifiers == EventFlags.EVENTFLAG_SHIFT_DOWN or EventFlags.EVENTFLAG_CONTROL_DOWN
                    && event.windows_key_code == KeyEvent.VK_O) {
                    search(selectedText)
                    return true
                }
                if (event.modifiers == EventFlags.EVENTFLAG_ALT_DOWN or EventFlags.EVENTFLAG_CONTROL_DOWN
                    && event.windows_key_code == KeyEvent.VK_O) {
                    browsePage(selectedText)
                    return true
                }
                if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN
                    && event.windows_key_code == KeyEvent.VK_K) {
                    //object : KoinComponent{ val viewModel: WebTabViewModel by inject() }.viewModel.switchDevTools()
                    val devToolsDialog = JDialog()
                    devToolsDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE)
                    devToolsDialog.setSize(800, 600)
                    devToolsDialog.add(browser?.devTools?.uiComponent)
                    devToolsDialog.isVisible = true
                    return true
                }
                if (event.modifiers == EventFlags.EVENTFLAG_SHIFT_DOWN
                    && event.windows_key_code == KeyEvent.VK_P) {
                    val folder = Paths.get("user/screenshot")
                    if (Files.exists(folder).not()) {
                        Files.createDirectories(folder)
                    }
                    val outputStream = Files.newOutputStream(folder.resolve("${UUID.randomUUID().toString()}.png"))
                    outputStream.use {
                        browser ?: return true
                        val p = Point(0, 0)
                        SwingUtilities.convertPointToScreen(p, browser.uiComponent)
                        val region = browser.uiComponent.bounds;
                        region.x = p.x;
                        region.y = p.y;

                        val screenshot = Robot().createScreenCapture(region)
                        ImageIO.write(screenshot, "png", it)
                    }
                    return true
                }
                return super.onKeyEvent(browser, event)
            }
        })
        client.addContextMenuHandler(object : CefContextMenuHandlerAdapter() {
            override fun onBeforeContextMenu(
                browser: CefBrowser?,
                frame: CefFrame?,
                params: CefContextMenuParams?,
                model: CefMenuModel?
            ) {
                super.onBeforeContextMenu(browser, frame, params, model)
                selectedText = params?.selectionText ?: ""

                model?.addItem(401, "リロード")
                model?.addItem(408, "ブックマークに追加")
                if (params?.linkUrl.isNullOrBlank().not()) {
                    model?.addItem(402, "別タブで開く")
                    model?.addItem(403, "バックグラウンドで開く")
                    model?.addItem(404, "リンクをコピー")
                }
                if (params?.sourceUrl.isNullOrBlank().not()) {
                    model?.addItem(407, "ダウンロード")
                    model?.addItem(409, "コピー")
                }
                if (params?.linkUrl.isNullOrBlank() && params?.sourceUrl.isNullOrBlank()) {
                    model?.addItem(410, "ページのリンクをコピー")
                    model?.addItem(411, "Markdown のリンクをコピー")
                }
                if (selectedText.isNotBlank()) {
                    model?.addItem(405, "選択したテキストを検索")
                }
                model?.addItem(414, "ブラウザーで開く")
                model?.addItem(406, "ズーム率をリセット")
                model?.addItem(412, "PDF で保存")
            }

            override fun onContextMenuCommand(
                browser: CefBrowser?,
                frame: CefFrame?,
                params: CefContextMenuParams?,
                commandId: Int,
                eventFlags: Int
            ): Boolean {
                return when (commandId) {
                    401 -> {
                        lastId?.let { reload(it) }
                        return true
                    }
                    402 -> {
                        params?.linkUrl?.let {
                            object : KoinComponent{ val viewModel: MainViewModel by inject() }.viewModel
                                .openUrl(it, false)
                        }
                        return true
                    }
                    403 -> {
                        params?.linkUrl?.let {
                            object : KoinComponent{ val viewModel: MainViewModel by inject() }.viewModel
                                .openUrl(it, true)
                        }
                        return true
                    }
                    404 -> {
                        params?.linkUrl?.let {
                            ClipboardPutterService().invoke(it)
                        }
                        return true
                    }
                    405 -> {
                        search(selectedText)
                        return true
                    }
                    406 -> {
                        latestBrowser()?.let {
                            it.zoomLevel = 0.0
                        }
                        return true
                    }
                    407 -> {
                        latestBrowser()?.startDownload(params?.sourceUrl)
                        return true
                    }
                    408 -> {
                        addBookmark(params)
                        return true
                    }
                    409 -> {
                        // TODO
                        return true
                    }
                    410 -> {
                        ClipboardPutterService().invoke(params?.pageUrl)
                        return true
                    }
                    411 -> {
                        val mainViewModel = object : KoinComponent{ val vm: MainViewModel by inject() }.vm
                        ClipboardPutterService().invoke(
                            "[${mainViewModel.tabs[mainViewModel.selected.value].title()}](${params?.pageUrl})"
                        )
                        return true
                    }
                    412 -> {
                        lastId?.let { printPdf(it) }
                        return true
                    }
                    414 -> {
                        browsePage(params?.linkUrl ?: params?.sourceUrl ?: selectedText)
                        return true
                    }
                    else -> super.onContextMenuCommand(browser, frame, params, commandId, eventFlags)
                }
            }

        })
    }

    private fun browsePage(selectedText: String) {
        val urlString = if (selectedText.startsWith("http://") || selectedText.startsWith("https://")) {
            selectedText
        } else {
            latestBrowser()?.url
        } ?: return
        Desktop.getDesktop().browse(URI(urlString))
    }

    private fun search(text: String) {
        if (text.isBlank()) {
            return
        }
        val urlOpenerService = UrlOpenerService()
        if (text.startsWith("http://") || text.startsWith("https://")) {
            urlOpenerService.invoke(text)
            return
        }
        urlOpenerService("https://search.yahoo.co.jp/search?p=${URLEncoder.encode(text, StandardCharsets.UTF_8)}")
    }

    override fun onLayout(x: Int, y: Int, width: Int, height: Int) {
        latestBrowser()?.uiComponent?.setBounds(x, y, width, height)
    }

    private fun latestBrowser(): CefBrowser? = browsers.get(lastId)

    fun onMouseEvent(event: MouseEvent) {
        //latestBrowser()?.uiComponent?.onMouseEvent(event)
    }

    fun onMouseScrollEvent(event: MouseWheelEvent) {
        //latestBrowser()?.uiComponent?.onMouseScrollEvent(event)
    }

    fun onKeyEvent(event: KeyEvent) {
        /*if (cefFocus) {
            browser.onKeyEvent(event)
        }*/
    }

    private val browsers = mutableMapOf<String, CefBrowser>()

    override fun component(id: String, initialUrl: String): Component {
        return getBrowser(id, initialUrl).uiComponent
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
        lastId = id
        return browser
    }

    private fun addBookmark(params: CefContextMenuParams? = null) {
        val mainViewModel = object : KoinComponent{ val vm: MainViewModel by inject() }.vm

        val item = when {
            params?.linkUrl != null && params.linkUrl.isNotBlank() -> makeBookmarkItemWithUrl(params.linkUrl)
            params?.sourceUrl != null && params.sourceUrl.isNotBlank() -> makeBookmarkItemWithUrl(params.sourceUrl)
            params?.pageUrl != null && params.pageUrl.isNotBlank() -> Bookmark(mainViewModel.tabs[mainViewModel.selected.value].title(), url = params.pageUrl)
            else -> Bookmark(mainViewModel.tabs[mainViewModel.selected.value].title(), url = latestBrowser()?.url ?: "")
        } ?: return
        object : KoinComponent{ val repository: BookmarkRepository by inject() }.repository.add(item)
        mainViewModel.showSnackbar("Add bookmark: $item")
    }

    private fun makeBookmarkItemWithUrl(url: String): Bookmark {
        return Bookmark(url, url = url)
    }

    fun printPdf(id: String) = browsers.get(id)?.let {
        it.printToPDF("${id}.pdf", null, null)
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