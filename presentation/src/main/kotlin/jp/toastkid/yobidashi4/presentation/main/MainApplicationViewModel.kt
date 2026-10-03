/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main

import jp.toastkid.yobidashi4.domain.service.notification.ScheduledNotification
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent

@Factory
class MainApplicationViewModel(
    private val mainViewModel: MainViewModel,
    private val notification: ScheduledNotification
) : KoinComponent {

    fun windowVisible() = mainViewModel.windowVisible()

    suspend fun startNotification() {
        notification.start()
    }

    suspend fun startReceiveNotification() {
        notification
            .notificationFlow()
            .collect(mainViewModel::sendNotification)
    }

    fun exitApplicationIfNeed(exitApplication: () -> Unit) {
        if (mainViewModel.windowVisible().not()) {
            exitApplication()
        }
    }

}