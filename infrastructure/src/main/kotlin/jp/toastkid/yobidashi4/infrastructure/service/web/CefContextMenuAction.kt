package jp.toastkid.yobidashi4.infrastructure.service.web

import java.net.URL
import javax.imageio.ImageIO
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.search.SearchSite
import jp.toastkid.yobidashi4.infrastructure.model.web.ContextMenu
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
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
            ContextMenu.RELOAD.id -> {
                browser?.reload()
            }

            ContextMenu.OPEN_OTHER_TAB.id -> {
                params?.linkUrl?.let {
                    viewModel.openUrl(it, false)
                }
            }

            ContextMenu.OPEN_BACKGROUND.id -> {
                params?.linkUrl?.let {
                    viewModel.openUrl(it, true)
                }
            }

            ContextMenu.CLIP_LINK.id -> {
                params?.linkUrl?.let {
                    ClipboardPutterService().invoke(it)
                }
            }

            ContextMenu.SEARCH_WITH_SELECTED_TEXT.id -> {
                viewModel.webSearch(selectedText)
            }

            ContextMenu.RESET_ZOOM.id -> {
                browser?.let {
                    it.zoomLevel = 0.0
                }
            }

            ContextMenu.DOWNLOAD.id -> {
                browser?.startDownload(params?.sourceUrl)
            }

            ContextMenu.ADD_BOOKMARK.id -> {
                BookmarkInsertion()(params, browser?.url)
            }

            ContextMenu.CLIP_IMAGE.id -> {
                val image = ImageIO.read(URL(params?.sourceUrl)) ?: return
                ClipboardPutterService().invoke(image)
            }

            ContextMenu.CLIP_PAGE_LINK.id -> {
                ClipboardPutterService().invoke(params?.linkUrl ?: params?.sourceUrl ?: params?.pageUrl)
            }

            ContextMenu.CLIP_AS_MARKDOWN_LINK.id -> {
                ClipboardPutterService().invoke("[${viewModel.currentTab()?.title()}](${params?.pageUrl})")
            }

            ContextMenu.SAVE_AS_PDF.id -> {
                browser?.printToPDF("${browser.identifier}.pdf", null, null)
            }

            ContextMenu.OPEN_WITH_OTHER_BROWSER.id -> {
                viewModel.browseUri(params?.linkUrl ?: params?.sourceUrl ?: selectedText)
            }

            ContextMenu.SEARCH_WITH_IMAGE.id -> {
                params?.sourceUrl?.let {
                    viewModel.openUrl(SearchSite.SEARCH_WITH_IMAGE.make(it).toString(), false)
                }
            }

            ContextMenu.CLIP_TEXT.id -> {
                params?.selectionText?.let {
                    ClipboardPutterService().invoke(it)
                }
            }

            ContextMenu.DEVELOPER_TOOL.id -> {
                val webTabId = (viewModel.currentTab() as? WebTab)?.id() ?: return
                object : KoinComponent { val viewModel: WebTabViewModel by inject() }.viewModel.switchDevTools(webTabId)
            }

            else -> Unit
        }
    }

}