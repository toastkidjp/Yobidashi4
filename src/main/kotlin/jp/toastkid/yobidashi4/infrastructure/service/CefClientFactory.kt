package jp.toastkid.yobidashi4.infrastructure.service

import java.awt.Desktop
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.Locale
import java.util.stream.Collectors
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.model.web.user_agent.UserAgent
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import jp.toastkid.yobidashi4.domain.service.web.WebIconLoaderService
import jp.toastkid.yobidashi4.infrastructure.service.web.CefContextMenuAction
import jp.toastkid.yobidashi4.infrastructure.service.web.CefContextMenuFactory
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
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

            private val webIconLoaderService = WebIconLoaderService()

            override fun onLoadingStateChange(
                browser: CefBrowser?,
                isLoading: Boolean,
                canGoBack: Boolean,
                canGoForward: Boolean
            ) {
                super.onLoadingStateChange(browser, isLoading, canGoBack, canGoForward)
                if (isLoading.not() && browser?.url?.startsWith("http") == true) {
                    browser.getSource {
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
                val downloadFolder = Path.of("user/download")
                if (Files.exists(downloadFolder).not()) {
                    Files.createDirectories(downloadFolder)
                }
                if (suggestedName == null) {
                    return
                }
                callback?.Continue(downloadFolder.resolve(suggestedName).absolutePathString(), false)
            }
        })

        client.addKeyboardHandler(object : CefKeyboardHandlerAdapter() {

            private val keyboardShortcutProcessor = CefKeyboardShortcutProcessor(
                this@CefClientFactory::search,
                this@CefClientFactory::addBookmark,
                { selectedText },
                this@CefClientFactory::browsePage
            )

            override fun onKeyEvent(browser: CefBrowser?, event: CefKeyboardHandler.CefKeyEvent?): Boolean {
                event ?: return false

                if (keyboardShortcutProcessor(browser, event)) {
                    return true
                }

                return super.onKeyEvent(browser, event)
            }
        })
        client.addContextMenuHandler(object : CefContextMenuHandlerAdapter() {

            private val cefContextMenuFactory = CefContextMenuFactory()

            override fun onBeforeContextMenu(
                browser: CefBrowser?,
                frame: CefFrame?,
                params: CefContextMenuParams?,
                model: CefMenuModel?
            ) {
                super.onBeforeContextMenu(browser, frame, params, model)
                selectedText = params?.selectionText ?: ""

                cefContextMenuFactory.invoke(params, model)
            }

            private val cefContextMenuAction = CefContextMenuAction()

            override fun onContextMenuCommand(
                browser: CefBrowser?,
                frame: CefFrame?,
                params: CefContextMenuParams?,
                commandId: Int,
                eventFlags: Int
            ): Boolean {
                cefContextMenuAction.invoke(browser, params, selectedText, commandId)

                return super.onContextMenuCommand(browser, frame, params, commandId, eventFlags)
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
            params?.linkUrl != null && params.linkUrl.isNotBlank() ->
                makeBookmarkItemWithUrl(params.linkUrl)
            params?.sourceUrl != null && params.sourceUrl.isNotBlank() ->
                makeBookmarkItemWithUrl(params.sourceUrl)
            params?.pageUrl != null && params.pageUrl.isNotBlank() ->
                Bookmark(mainViewModel.currentTab()?.title() ?: "", url = params.pageUrl)
            else ->
                Bookmark(mainViewModel.currentTab()?.title() ?: "", url = latestBrowser()?.url ?: "")
        }
        object : KoinComponent{ val repository: BookmarkRepository by inject() }.repository.add(item)
        mainViewModel.showSnackbar("Add bookmark: $item")
    }

    private fun makeBookmarkItemWithUrl(url: String): Bookmark {
        return Bookmark(url, url = url)
    }

}