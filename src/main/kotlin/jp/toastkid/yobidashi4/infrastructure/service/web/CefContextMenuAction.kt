package jp.toastkid.yobidashi4.infrastructure.service.web

import java.awt.Desktop
import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.imageio.ImageIO
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark
import jp.toastkid.yobidashi4.domain.model.web.search.SearchSite
import jp.toastkid.yobidashi4.domain.repository.BookmarkRepository
import jp.toastkid.yobidashi4.presentation.editor.legacy.service.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import jp.toastkid.yobidashi4.presentation.viewmodel.web.WebTabViewModel
import org.cef.browser.CefBrowser
import org.cef.callback.CefContextMenuParams
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CefContextMenuAction : KoinComponent {

    private val viewModel: MainViewModel by inject()

    operator fun invoke(
        browser: CefBrowser?,
        params: CefContextMenuParams?,
        selectedText: String,
        commandId: Int
        ) {
        when (commandId) {
            401 -> {
                browser?.reload()
            }

            402 -> {
                params?.linkUrl?.let {
                    viewModel.openUrl(it, false)
                }
                
            }

            403 -> {
                params?.linkUrl?.let {
                    // TODO
                    viewModel.openUrl(it, true)
                    val webTab = viewModel.tabs.last() as? WebTab ?: return@let
                    object : KoinComponent {
                        val webViewPool: WebViewPool by inject()
                    }.webViewPool.component(
                        webTab.id(), webTab.url()
                    )
                }
                
            }

            404 -> {
                params?.linkUrl?.let {
                    ClipboardPutterService().invoke(it)
                }
                
            }

            405 -> {
                search(selectedText)
                
            }

            406 -> {
                browser?.let {
                    it.zoomLevel = 0.0
                }
                
            }

            407 -> {
                browser?.startDownload(params?.sourceUrl)
                
            }

            408 -> {
                addBookmark(browser, params)
                
            }

            409 -> {
                val image = ImageIO.read(URL(params?.sourceUrl)) ?: return
                ClipboardPutterService().invoke(image)
            }

            410 -> {
                ClipboardPutterService().invoke(params?.linkUrl ?: params?.sourceUrl ?: params?.pageUrl)
                
            }

            411 -> {
                ClipboardPutterService().invoke("[${viewModel.currentTab()?.title()}](${params?.pageUrl})")
                
            }

            412 -> {
                browser?.printToPDF("${browser.identifier}.pdf", null, null)
            }

            414 -> {
                browsePage(browser, params?.linkUrl ?: params?.sourceUrl ?: selectedText)
                
            }

            415 -> {
                params?.sourceUrl?.let {
                    viewModel.openUrl(SearchSite.SEARCH_WITH_IMAGE.make(it).toString(), false)
                }
            }

            416 -> {
                params?.selectionText?.let {
                    ClipboardPutterService().invoke(it)
                }
                
            }

            417 -> {
                (viewModel.currentTab() as? WebTab)?.id()?.let { id ->
                    object : KoinComponent {
                        val viewModel: WebTabViewModel by inject()
                    }.viewModel.switchDevTools(id)
                }
            }

            else -> Unit
        }
    }

    private fun browsePage(browser: CefBrowser?, selectedText: String) {
        val urlString = if (selectedText.startsWith("http://") || selectedText.startsWith("https://")) {
            selectedText
        } else {
            browser?.url
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

    private fun addBookmark(browser: CefBrowser?, params: CefContextMenuParams? = null) {
        val mainViewModel = object : KoinComponent{ val vm: MainViewModel by inject() }.vm

        val item = when {
            params?.linkUrl != null && params.linkUrl.isNotBlank() ->
                makeBookmarkItemWithUrl(params.linkUrl)
            params?.sourceUrl != null && params.sourceUrl.isNotBlank() ->
                makeBookmarkItemWithUrl(params.sourceUrl)
            params?.pageUrl != null && params.pageUrl.isNotBlank() ->
                Bookmark(mainViewModel.currentTab()?.title() ?: "", url = params.pageUrl)
            else ->
                Bookmark(mainViewModel.currentTab()?.title() ?: "", url = browser?.url ?: "")
        }
        object : KoinComponent{ val repository: BookmarkRepository by inject() }.repository.add(item)
        mainViewModel.showSnackbar("Add bookmark: $item")
    }

    private fun makeBookmarkItemWithUrl(url: String): Bookmark {
        return Bookmark(url, url = url)
    }

}