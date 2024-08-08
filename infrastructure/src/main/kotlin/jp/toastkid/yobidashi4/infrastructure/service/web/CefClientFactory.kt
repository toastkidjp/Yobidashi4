package jp.toastkid.yobidashi4.infrastructure.service.web

import java.awt.event.KeyEvent
import java.util.concurrent.atomic.AtomicReference
import javax.swing.SwingUtilities
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.web.ad.AdHosts
import jp.toastkid.yobidashi4.infrastructure.service.web.download.DownloadFolder
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.cef.CefClient
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.callback.CefBeforeDownloadCallback
import org.cef.callback.CefContextMenuParams
import org.cef.callback.CefDownloadItem
import org.cef.callback.CefMenuModel
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

class CefClientFactory : KoinComponent {

    private val viewModel : MainViewModel by inject()

    private val cefAppFactory = CefAppFactory()

    private val adHosts = AdHosts.make()

    operator fun invoke(): CefClient {
        val selectedText = AtomicReference("")

        val client = cefAppFactory.invoke().createClient()
        client.addLoadHandler(object : CefLoadHandlerAdapter() {

            private val webIconLoaderService = WebIconLoaderServiceImplementation()

            override fun onLoadingStateChange(
                browser: CefBrowser?,
                isLoading: Boolean,
                canGoBack: Boolean,
                canGoForward: Boolean
            ) {
                super.onLoadingStateChange(browser, isLoading, canGoBack, canGoForward)
                if (browser == null) {
                    return
                }

                if (isLoading.not() && browser.url?.startsWith("http") == true) {
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
                browser ?: return
                val id = object : KoinComponent { val pool: WebViewPool by inject() }.pool.findId(browser) ?: return
                viewModel.updateWebTab(id, title, browser.url)
            }
        })
        client.addDownloadHandler(object : CefDownloadHandlerAdapter() {
            override fun onBeforeDownload(
                browser: CefBrowser?,
                downloadItem: CefDownloadItem?,
                suggestedName: String?,
                callback: CefBeforeDownloadCallback?
            ) {
                callback ?: return

                val downloadFolder = DownloadFolder()
                downloadFolder.makeIfNeed()

                val assignAbsolutePath = downloadFolder.assignAbsolutePath(suggestedName) ?: return
                callback.Continue(assignAbsolutePath, false)
                return
            }
        })

        client.addKeyboardHandler(object : CefKeyboardHandlerAdapter() {

            private val keyboardShortcutProcessor = CefKeyboardShortcutProcessor(selectedText::get)

            override fun onKeyEvent(browser: CefBrowser?, event: CefKeyboardHandler.CefKeyEvent?): Boolean {
                if (browser == null || event == null) {
                    return false
                }

                if (keyboardShortcutProcessor(browser, event.type, event.modifiers, event.windows_key_code)) {
                    return true
                }

                if (event.type != CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_RAWKEYDOWN) {
                    return false
                }

                val windowForComponent = SwingUtilities.windowForComponent(browser.uiComponent)
                val modifiers = when (event.modifiers) {
                    EventFlags.EVENTFLAG_CONTROL_DOWN -> KeyEvent.CTRL_DOWN_MASK
                    EventFlags.EVENTFLAG_SHIFT_DOWN -> KeyEvent.SHIFT_DOWN_MASK
                    EventFlags.EVENTFLAG_ALT_DOWN -> KeyEvent.ALT_DOWN_MASK
                    else -> null
                } ?: return false

                windowForComponent.dispatchEvent(
                    KeyEvent(
                        browser.uiComponent,
                        KeyEvent.KEY_PRESSED,
                        System.currentTimeMillis(),
                        modifiers,
                        event.windows_key_code,
                        event.character
                    )
                )

                return false
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
                selectedText.set(params?.selectionText ?: "")

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
                cefContextMenuAction.invoke(browser, params, selectedText.get(), commandId)

                return super.onContextMenuCommand(browser, frame, params, commandId, eventFlags)
            }

        })

        return client
    }

}