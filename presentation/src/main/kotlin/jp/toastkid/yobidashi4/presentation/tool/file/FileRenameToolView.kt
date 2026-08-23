/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.tool.file

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_image
import jp.toastkid.yobidashi4.presentation.component.SingleLineTextField
import org.jetbrains.compose.resources.vectorResource

@Composable
fun FileRenameToolView() {
    val viewModel = remember { FileRenameToolViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Column {
            SingleLineTextField(
                viewModel.input(),
                "Base file name",
                viewModel::clearInput,
                modifier = Modifier.onKeyEvent(viewModel::onKeyEvent)
            )

            if (viewModel.input().text.isNotBlank()) {
                Text("Renamed file name: ", modifier = Modifier.padding(start = 8.dp))
                Text(viewModel.renamedSampleFileName(), modifier = Modifier.padding(start = 8.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = viewModel::switchUseResize)
            ) {
                Checkbox(
                    checked = viewModel.useResize().collectAsState().value,
                    onCheckedChange = { viewModel.switchUseResize() },
                    modifier = Modifier.padding(8.dp)
                )
                Text("Resize to 50%")
            }

            Row {
                Button(
                    onClick = viewModel::clearPaths,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Clear files")
                }
                Button(
                    onClick = { viewModel.rename() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Rename")
                }
            }

            Row {
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

                    if (viewModel.items().isEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.align(Alignment.Center).padding(8.dp)
                        ) {
                            Icon(vectorResource(Res.drawable.ic_image), contentDescription = "icon")
                            Text("Drop image files.", modifier = Modifier.padding(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                val onPrimary = MaterialTheme.colors.onPrimary
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(viewModel.listState()),
                    modifier = Modifier.fillMaxHeight()
                        .drawBehind { drawRect(onPrimary) }
                )
            }
        }
    }

    DisposableEffect(Unit) {
        viewModel.collectDroppedPaths()

        onDispose(viewModel::dispose)
    }
}
