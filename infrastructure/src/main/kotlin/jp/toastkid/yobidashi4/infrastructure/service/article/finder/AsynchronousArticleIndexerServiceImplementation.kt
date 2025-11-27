/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.article.finder.AsynchronousArticleIndexerService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

@Single
class AsynchronousArticleIndexerServiceImplementation : AsynchronousArticleIndexerService, KoinComponent {

    private val indexFolder = Path.of("temporary/finder/index")

    private val setting: Setting by inject()

    private val dataFolder = setting.articleFolderPath()

    override fun invoke(dispatcher: CoroutineDispatcher) {
        if (Files.exists(dataFolder).not()) {
            return
        }

        CoroutineScope(dispatcher).launch {
            val indexer = FullTextSearchIndexer(indexFolder)
            try {
                indexer.createIndex(dataFolder)
            } catch (e: IOException) {
                LoggerFactory.getLogger(javaClass).error("Indexing error", e)
            } finally {
                indexer.close()
            }
        }
    }

}