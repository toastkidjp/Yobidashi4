/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.web.bookmark

import jp.toastkid.yobidashi4.domain.model.tab.WebTab

data class Bookmark(
    val title: String = "",
    val url: String = "",
    val favicon: String = "",
    val parent: String = "root",
    val folder: Boolean = false
) {

    fun toTsv() = "${title}\t${url}"

    companion object {
        fun fromWebTab(webTab: WebTab): Bookmark {
            return Bookmark(webTab.title(), webTab.url())
        }

    }

}