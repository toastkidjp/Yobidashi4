/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.repository.notification

import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent

interface NotificationEventRepository {

    fun add(event: NotificationEvent)

    fun readAll(): List<NotificationEvent>

    fun update(index: Int, event: NotificationEvent)

    fun deleteAt(index: Int)

    fun delete(event: NotificationEvent)

    fun clear()

}