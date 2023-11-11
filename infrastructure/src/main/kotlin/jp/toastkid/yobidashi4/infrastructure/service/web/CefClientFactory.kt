package jp.toastkid.yobidashi4.infrastructure.service.web

import java.awt.Desktop
import java.io.File
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.web.ad.AdHosts
import jp.toastkid.yobidashi4.domain.model.web.user_agent.UserAgent
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
) : KoinComponent {

    private val appSetting : Setting by inject()

    private val viewModel : MainViewModel by inject()

    operator fun invoke(): CefClient {
        val builder = CefAppBuilder()
        builder.setInstallDir(File("jcef-bundle")) //Default
        builder.setProgressHandler(ConsoleProgressHandler()) //Default
        CefApp.addAppHandler(object : CefAppHandlerAdapter(arrayOf("--disable-gpu")) {
            override fun onBeforeCommandLineProcessing(processType: String?, commandLine: CefCommandLine?) {
                if (processType.isNullOrEmpty()) {
                    commandLine?.appendSwitchWithValue("enable-media-stream", "true")
                    if (appSetting.darkMode()) {
                        commandLine?.appendSwitchWithValue("blink-settings", "forceDarkModeInversionAlgorithm=1,forceDarkModeEnabled=true")
                    }
                }
                super.onBeforeCommandLineProcessing(processType, commandLine)
            }
        })

        CefSettingsApplier().invoke(builder.cefSettings, UserAgent.findByName(appSetting.userAgentName()).text())

        val adHosts = AdHosts.make()

        var selectedText = ""

        val cefApp = builder.build()
        val client = cefApp.createClient()
        client.addLoadHandler(object : CefLoadHandlerAdapter() {

            private val webIconLoaderService = WebIconLoaderServiceImplementation()

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
                        if (adHosts.contains(request?.url)) {
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

        if (text.startsWith("http://") || text.startsWith("https://")) {
            viewModel.openUrl(text, false)
            return
        }
        viewModel.openUrl("https://search.yahoo.co.jp/search?p=${URLEncoder.encode(text, StandardCharsets.UTF_8)}", false)
    }

}