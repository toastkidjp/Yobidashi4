/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.browser

import java.awt.Component

interface WebViewPool {

    fun component(id: String, initialUrl: String): Component

    fun dispose(id: String)

    fun disposeAll()

    fun find(id: String, text: String, forward: Boolean)

    fun clearFind(id: String)

    fun reload(id: String)

    fun switchDevTools(id: String)

    fun findId(browser: Any): String?

}
