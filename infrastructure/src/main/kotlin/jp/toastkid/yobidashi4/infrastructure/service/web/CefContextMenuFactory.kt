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

        ContextMenu.values().filter {
            return@filter when (it.context) {
                Context.IMAGE -> params?.sourceUrl.isNullOrBlank().not()
                Context.LINK -> params?.pageUrl.isNullOrBlank().not()
                Context.PLAIN_TEXT -> params?.linkUrl.isNullOrBlank() && params?.sourceUrl.isNullOrBlank()
                else -> return@filter true
            }
        }.forEach {
            model.addItem(it.id, it.text)
        }
    }

}