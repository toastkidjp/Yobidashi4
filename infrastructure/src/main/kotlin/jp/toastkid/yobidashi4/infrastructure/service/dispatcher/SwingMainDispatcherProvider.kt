/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.dispatcher

import jp.toastkid.yobidashi4.domain.service.dispatcher.UiThreadDispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.koin.core.annotation.Single
import java.util.concurrent.Executor
import javax.swing.SwingUtilities

@Single
class SwingMainDispatcherProvider : UiThreadDispatcherProvider {

    override operator fun invoke(): CoroutineDispatcher = Executor { command ->
        SwingUtilities.invokeLater(command)
    }.asCoroutineDispatcher()

}
