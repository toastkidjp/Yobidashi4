/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.main

import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.infrastructure.di.DependencyInjectionContainer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppCloserAction : KoinComponent {

    private val setting: Setting by inject()

    private val webViewPool: WebViewPool by inject()

    operator fun invoke() {
        setting.save()

        DependencyInjectionContainer.stop()

        webViewPool.disposeAll()
    }

}