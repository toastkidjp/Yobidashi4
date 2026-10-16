/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.repository.web.history

import jp.toastkid.yobidashi4.domain.model.web.history.WebHistory

interface WebHistoryRepository {

    fun add(title: String, url: String)

    fun storeAll(items: List<WebHistory>)

    fun delete(item: WebHistory)

    fun readAll(): List<WebHistory>

    fun clear()

}