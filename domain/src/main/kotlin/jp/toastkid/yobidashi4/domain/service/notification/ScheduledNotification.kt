/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.notification

import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import kotlinx.coroutines.flow.SharedFlow

interface ScheduledNotification {

    fun notificationFlow(): SharedFlow<NotificationEvent>

    suspend fun start(delay: Long = 10_000)

}