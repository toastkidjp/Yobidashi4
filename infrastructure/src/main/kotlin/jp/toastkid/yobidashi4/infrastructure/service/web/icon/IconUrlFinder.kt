/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.web.icon

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class IconUrlFinder {

    operator fun invoke(htmlSource: String) =
        Jsoup.parse(htmlSource)
            .select("link")
            .filter(::extractIcon)
            .map { it.attr("href") }

    private fun extractIcon(elem: Element) = elem.attr("rel").contains("icon")

}