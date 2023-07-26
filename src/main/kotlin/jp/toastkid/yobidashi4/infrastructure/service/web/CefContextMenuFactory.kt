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
        ContextMenu.values().filter {
            when (it.context) {
                Context.IMAGE -> {
                    return@filter params?.sourceUrl.isNullOrBlank().not()
                }
                Context.LINK -> {
                    return@filter params?.linkUrl.isNullOrBlank().not()
                }
                Context.PLAIN_TEXT -> {
                    return@filter params?.linkUrl.isNullOrBlank() && params?.sourceUrl.isNullOrBlank()
                }
                else -> return@filter true
            }
        }.forEach {
            model?.addItem(it.id, it.text)
        }
    }

}