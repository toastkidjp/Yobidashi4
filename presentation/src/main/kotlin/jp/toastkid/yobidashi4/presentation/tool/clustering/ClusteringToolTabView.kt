/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.tool.clustering

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_edit
import jp.toastkid.yobidashi4.library.resources.ic_markdown
import jp.toastkid.yobidashi4.presentation.component.HoverHighlightRow
import org.jetbrains.compose.resources.painterResource

@Composable
fun ClusteringToolTabView() {
    val viewModel = remember { ClusteringToolTabViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Box {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Row {
                        Button(
                            onClick = viewModel::clearPaths,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text("Clear files")
                        }
                        Button(
                            onClick = viewModel::invoke,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text("Invoke")
                        }
                    }

                    Box {
                        LazyColumn(state = viewModel.listState()) {
                            items(viewModel.items()) { path ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text(path.fileName.toString())
                                    Text(
                                        "x",
                                        color = MaterialTheme.colors.secondary,
                                        modifier = Modifier.padding(8.dp)
                                            .clickable {
                                                viewModel.remove(path)
                                            }
                                            .semantics { contentDescription = "Delete ${path.fileName}" }
                                    )
                                }
                            }
                        }
                        VerticalScrollbar(
                            adapter = rememberScrollbarAdapter(viewModel.listState()),
                            modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
                        )
                    }
                }

                if (viewModel.result().isNotEmpty()) {
                    Column(modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                    ) {
                        viewModel.result().forEach {
                            Text(
                                "${it.key}: ${it.value.size} entries",
                                modifier = Modifier.padding(8.dp)
                            )

                            it.value.forEach { title ->
                                HoverHighlightRow { textColor ->
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_markdown),
                                        contentDescription = "Open preview",
                                        tint = MaterialTheme.colors.secondary,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                            .padding(start = 8.dp)
                                            .clickable {
                                                viewModel.openMarkdownPreview(title)
                                            }
                                    )

                                    Icon(
                                        painter = painterResource(Res.drawable.ic_edit),
                                        contentDescription = "Open file",
                                        tint = MaterialTheme.colors.secondary,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                            .padding(start = 4.dp)
                                            .clickable {
                                                viewModel.edit(title)
                                            }
                                    )

                                    Text(title)
                                }
                            }
                        }
                    }
                }
            }

            if (viewModel.processing()) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp).align(Alignment.Center)
                )
            }
        }
    }

    DisposableEffect(Unit) {
        viewModel.collectDroppedPaths()

        onDispose(viewModel::dispose)
    }
}
