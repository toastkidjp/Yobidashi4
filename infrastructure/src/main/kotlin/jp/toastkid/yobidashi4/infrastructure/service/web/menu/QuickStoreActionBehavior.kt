/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.web.menu

import jp.toastkid.yobidashi4.domain.model.download.DownloadFolder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL
import java.nio.file.Files

class QuickStoreActionBehavior {

    operator fun invoke(sourceUrl: URL, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        @Suppress("BlockingMethodInNonBlockingContext")
        CoroutineScope(dispatcher).launch {
            val image = sourceUrl.openStream().readAllBytes() ?: return@launch

            val downloadFolder = DownloadFolder()
            downloadFolder.makeIfNeed()

            Files.write(downloadFolder.assignQuickStorePath(sourceUrl.file), image)
        }
    }
}