/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.io

import jp.toastkid.yobidashi4.domain.service.io.IoContextProvider
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Single

@Single
class IoContextProviderImplementation : IoContextProvider {

    override fun invoke() = Dispatchers.IO

}
