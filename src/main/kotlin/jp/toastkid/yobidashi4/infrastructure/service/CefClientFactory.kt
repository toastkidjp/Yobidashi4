package jp.toastkid.yobidashi4.infrastructure.service

import java.awt.Desktop
import java.awt.Point
import java.awt.Robot
import java.awt.event.KeyEvent
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Locale
import java.util.UUID
import java.util.stream.Collectors
import javax.imageio.ImageIO
import javax.swing.JDialog
import javax.swing.SwingUtilities
import javax.swing.WindowConstants
import jp.toastkid.yobidashi4.domain.model.browser.BrowserPool
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.model.web.search.SearchSite
import jp.toastkid.yobidashi4.domain.model.web.user_agent.UserAgent
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import jp.toastkid.yobidashi4.domain.service.web.WebIconLoaderService
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
import org.cef.callback.CefCommandLine
import org.cef.callback.CefContextMenuParams
import org.cef.callback.CefDownloadItem
import org.cef.callback.CefMenuModel
import org.cef.handler.CefAppHandlerAdapter
import org.cef.handler.CefContextMenuHandlerAdapter
import org.cef.handler.CefDisplayHandlerAdapter
import org.cef.handler.CefDownloadHandlerAdapter
import org.cef.handler.CefKeyboardHandler
import org.cef.handler.CefKeyboardHandlerAdapter
import org.cef.handler.CefLifeSpanHandlerAdapter
import org.cef.handler.CefLoadHandlerAdapter
import org.cef.handler.CefRequestHandlerAdapter
import org.cef.handler.CefResourceRequestHandler
import org.cef.handler.CefResourceRequestHandlerAdapter
import org.cef.misc.BoolRef
import org.cef.misc.EventFlags
import org.cef.network.CefRequest
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CefClientFactory(
    private val latestBrowser: () -> CefBrowser?,
    private val findId: (CefBrowser?) -> String?
) {

    operator fun invoke(): CefClient {
        val appSetting = object : KoinComponent { val s: Setting by inject() }.s

        val viewModel = object : KoinComponent { val viewModel: MainViewModel by inject() }.viewModel

        val builder = CefAppBuilder()
        builder.setInstallDir(File("jcef-bundle")) //Default
        builder.setProgressHandler(ConsoleProgressHandler()) //Default
        CefApp.addAppHandler(object : CefAppHandlerAdapter(arrayOf("--disable-gpu")) {
            override fun onBeforeCommandLineProcessing(process_type: String?, command_line: CefCommandLine?) {
                if (process_type.isNullOrEmpty()) {
                    command_line?.appendSwitchWithValue("enable-media-stream", "true")
                    if (appSetting.darkMode()) {
                        command_line?.appendSwitchWithValue("blink-settings", "forceDarkModeInversionAlgorithm=1,forceDarkModeEnabled=true")
                    }
                }
                super.onBeforeCommandLineProcessing(process_type, command_line)
            }
        })

        val settings = builder.cefSettings
        settings.windowless_rendering_enabled = false //Default - select OSR mode
        settings.background_color = settings.ColorType(80, 0, 0, 0)
        settings.user_agent = UserAgent.findByName(appSetting.userAgentName()).text()
        settings.locale = Locale.getDefault().language

        val adHosts = javaClass.classLoader.getResourceAsStream("web/ad_hosts.txt")?.use { stream ->
            return@use BufferedReader(InputStreamReader(stream)).use { reader ->
                reader.lines().collect(Collectors.toList())
            }
        } ?: emptyList()

        var selectedText = ""

        val cefApp = builder.build()
        val client = cefApp.createClient()
        client.addLoadHandler(object : CefLoadHandlerAdapter() {
            // TODO Impl load action
            private val webIconLoaderService = WebIconLoaderService()

            override fun onLoadingStateChange(
                browser: CefBrowser?,
                isLoading: Boolean,
                canGoBack: Boolean,
                canGoForward: Boolean
            ) {
                super.onLoadingStateChange(browser, isLoading, canGoBack, canGoForward)
                if (isLoading.not()) {
                    browser?.getSource {
                        webIconLoaderService.invoke(it, browser.url)
                    }
                }
            }
        })

        client.addLifeSpanHandler(object : CefLifeSpanHandlerAdapter() {
            override fun onBeforePopup(
                browser: CefBrowser?,
                frame: CefFrame?,
                target_url: String?,
                target_frame_name: String?
            ): Boolean {
                target_url ?: return true
                viewModel.openUrl(target_url, false)
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
                        viewModel.openUrl(it, true)
                    }
                    return true
                }
                return super.onOpenURLFromTab(browser, frame, target_url, user_gesture)
            }
        })
        client.addDisplayHandler(object : CefDisplayHandlerAdapter() {
            override fun onTitleChange(browser: CefBrowser?, title: String?) {
                title ?: return
                val id = findId(browser) ?: return
                viewModel.updateWebTab(id, title, browser?.url)
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
            override fun onKeyEvent(browser: CefBrowser?, event: CefKeyboardHandler.CefKeyEvent?): Boolean {
                event ?: return false
                if (event.type != CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP) {
                    return false
                }

                if (event.windows_key_code == KeyEvent.VK_BACK_SPACE) {
                    latestBrowser()?.let {
                        if (it.canGoBack()) {
                            it.goBack()
                            return true
                        }
                    }
                }

                if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == KeyEvent.VK_F) {
                    object : KoinComponent { val viewModel: WebTabViewModel by inject() }.viewModel.switchFind()
                    return true
                }
                if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == KeyEvent.VK_W) {
                    viewModel.closeCurrent()
                    return true
                }
                if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == KeyEvent.VK_P) {
                    printPdf()
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
                    devToolsDialog.defaultCloseOperation = WindowConstants.HIDE_ON_CLOSE
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

                model?.addItem(401, "????????????")
                model?.addItem(408, "???????????????????????????")
                if (params?.linkUrl.isNullOrBlank().not()) {
                    model?.addItem(402, "??????????????????")
                    model?.addItem(403, "?????????????????????????????????")
                    model?.addItem(404, "?????????????????????")
                }
                if (params?.sourceUrl.isNullOrBlank().not()) {
                    model?.addItem(407, "??????????????????")
                    model?.addItem(409, "??????????????????")
                    model?.addItem(415, "?????????????????????")
                }
                if (params?.linkUrl.isNullOrBlank() && params?.sourceUrl.isNullOrBlank()) {
                    model?.addItem(410, "?????????????????????????????????")
                    model?.addItem(411, "Markdown ????????????????????????")
                }
                if (selectedText.isNotBlank()) {
                    model?.addItem(416, "????????????????????????")
                    model?.addItem(405, "?????????????????????????????????")
                }
                model?.addItem(414, "????????????????????????")
                model?.addItem(406, "???????????????????????????")
                model?.addItem(412, "PDF ?????????")
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
                        latestBrowser()?.reload()
                        return true
                    }
                    402 -> {
                        params?.linkUrl?.let {
                            viewModel.openUrl(it, false)
                        }
                        return true
                    }
                    403 -> {
                        params?.linkUrl?.let {
                            // TODO
                            viewModel.openUrl(it, true)
                            val webTab = viewModel.tabs.last() as? WebTab ?: return true
                            object : KoinComponent { val browserPool: BrowserPool by inject() }.browserPool.component(
                                webTab.id(), webTab.url()
                            )
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
                        val image = ImageIO.read(URL(params?.sourceUrl)) ?: return true
                        ClipboardPutterService().invoke(image)
                        return true
                    }
                    410 -> {
                        ClipboardPutterService().invoke(params?.linkUrl ?: params?.sourceUrl ?: params?.pageUrl)
                        return true
                    }
                    411 -> {
                        ClipboardPutterService().invoke(
                            "[${viewModel.tabs[viewModel.selected.value].title()}](${params?.pageUrl})"
                        )
                        return true
                    }
                    412 -> {
                        printPdf()
                        return true
                    }
                    414 -> {
                        browsePage(params?.linkUrl ?: params?.sourceUrl ?: selectedText)
                        return true
                    }
                    415 -> {
                        params?.sourceUrl?.let {
                            viewModel.openUrl(SearchSite.SEARCH_WITH_IMAGE.make(it).toString(), false)
                        }
                        return true
                    }
                    416 -> {
                        params?.selectionText?.let {
                            ClipboardPutterService().invoke(it)
                        }
                        return true
                    }
                    else -> super.onContextMenuCommand(browser, frame, params, commandId, eventFlags)
                }
            }

        })

        return client
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
        val mainViewModel = object : KoinComponent{ val vm: MainViewModel by inject() }.vm
        if (text.startsWith("http://") || text.startsWith("https://")) {
            mainViewModel.openUrl(text, false)
            return
        }
        mainViewModel.openUrl("https://search.yahoo.co.jp/search?p=${URLEncoder.encode(text, StandardCharsets.UTF_8)}", false)
    }

    private fun addBookmark(params: CefContextMenuParams? = null) {
        val mainViewModel = object : KoinComponent{ val vm: MainViewModel by inject() }.vm

        val item = when {
            params?.linkUrl != null && params.linkUrl.isNotBlank() -> makeBookmarkItemWithUrl(params.linkUrl)
            params?.sourceUrl != null && params.sourceUrl.isNotBlank() -> makeBookmarkItemWithUrl(params.sourceUrl)
            params?.pageUrl != null && params.pageUrl.isNotBlank() -> Bookmark(mainViewModel.tabs[mainViewModel.selected.value].title(), url = params.pageUrl)
            else -> Bookmark(mainViewModel.tabs[mainViewModel.selected.value].title(), url = latestBrowser()?.url ?: "")
        }
        object : KoinComponent{ val repository: BookmarkRepository by inject() }.repository.add(item)
        mainViewModel.showSnackbar("Add bookmark: $item")
    }

    private fun makeBookmarkItemWithUrl(url: String): Bookmark {
        return Bookmark(url, url = url)
    }

    private fun printPdf() = latestBrowser()?.let {
        it.printToPDF("${it.identifier}.pdf", null, null)
    }

}