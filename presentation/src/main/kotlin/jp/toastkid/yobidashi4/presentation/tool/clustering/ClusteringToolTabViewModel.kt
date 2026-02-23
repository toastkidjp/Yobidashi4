/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.tool.clustering

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.domain.service.tool.clustering.KMeans
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name

class ClusteringToolTabViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val kMeans: KMeans by inject()

    private val articleFactory: ArticleFactory by inject()

    private val paths = mutableStateSetOf<Path>()

    private val processing = mutableStateOf(false)

    fun processing() = processing.value

    private val result = mutableStateMapOf<String, List<String>>()

    fun result() = result

    private val listState = LazyListState()

    fun items(): List<Path> = paths.toList()

    fun listState() = listState

    fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        if (paths.isEmpty()) {
            return
        }

        this.result.clear()

        CoroutineScope(dispatcher).launch {
            processing.value = true
            try {
                val docs = paths.map { it.name to Files.readString(it) }.toList()
                kMeans.invoke(docs).forEach(result::put)
                viewModel
                    .showSnackbar(
                        "Clustering completed!",
                        "Open folder",
                        ::openFolder
                    )
            } catch (e: Exception) {
                LoggerFactory.getLogger(javaClass).error("Clustering error.", e)
            } finally {
                processing.value = false
            }
        }
    }

    private fun openFolder() {
        viewModel.openFile(paths.first().parent)
    }

    fun onKeyEvent(it: KeyEvent): Boolean {
        if (it.type == KeyEventType.KeyDown && it.key == Key.Enter) {
            invoke()
            return true
        }

        return false
    }

    fun clearPaths() {
        paths.clear()
    }

    fun collectDroppedPaths() {
        viewModel.registerDroppedPathReceiver {
            paths.add(it)
        }
    }

    fun dispose() {
        viewModel.unregisterDroppedPathReceiver()
    }

    fun remove(path: Path) {
        paths.remove(path)
    }

    fun openMarkdownPreview(title: String) {
        val nextArticle = articleFactory.withTitle(title)
        viewModel.openPreview(nextArticle.path())
    }

    fun edit(title: String) {
        viewModel.editWithTitle(title)
    }

}