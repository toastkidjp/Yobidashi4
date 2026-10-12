/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
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

        val paramIsNotNull = params != null
        val enableSourceUrl = paramIsNotNull && params.sourceUrl.isNullOrBlank().not()
        val enablePageUrl = paramIsNotNull && params.pageUrl.isNullOrBlank().not()
        val usePlainTextMenu = paramIsNotNull && params.linkUrl.isNullOrBlank() && params.sourceUrl.isNullOrBlank()

        ContextMenu.entries.filter {
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