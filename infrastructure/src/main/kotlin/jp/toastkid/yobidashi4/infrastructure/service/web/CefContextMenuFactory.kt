package jp.toastkid.yobidashi4.infrastructure.service.web

import jp.toastkid.yobidashi4.infrastructure.model.web.Context
import jp.toastkid.yobidashi4.infrastructure.model.web.ContextMenu
import org.cef.callback.CefContextMenuParams
import org.cef.callback.CefMenuModel

class CefContextMenuFactory {

    operator fun invoke(
        params: CefContextMenuParams?,
        model: CefMenuModel?
    ) {
        if (model == null) {
            return
        }

        val enableSourceUrl = params != null && params.sourceUrl.isNullOrBlank().not()
        val enablePageUrl = params != null && params.pageUrl.isNullOrBlank().not()
        val usePlainTextMenu = params != null && params.linkUrl.isNullOrBlank() && params.sourceUrl.isNullOrBlank()

        ContextMenu.values().filter {
            return@filter when (it.context) {
                Context.IMAGE -> enableSourceUrl
                Context.LINK -> enablePageUrl
                Context.PLAIN_TEXT -> usePlainTextMenu
                else -> return@filter true
            }
        }.forEach {
            model.addItem(it.id, it.text)
        }
    }

}