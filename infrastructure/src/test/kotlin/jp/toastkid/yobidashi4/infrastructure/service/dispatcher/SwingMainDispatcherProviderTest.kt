/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.dispatcher

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SwingMainDispatcherProviderTest {

    @Test
    fun invoke() {
        val coroutineDispatcher = SwingMainDispatcherProvider().invoke()
        CoroutineScope(coroutineDispatcher).launch {
            assertTrue(true)
        }
    }

}