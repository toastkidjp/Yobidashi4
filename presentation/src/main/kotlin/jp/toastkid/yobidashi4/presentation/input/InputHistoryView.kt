/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.input

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.input.InputHistory
import jp.toastkid.yobidashi4.domain.model.tab.InputHistoryTab
import jp.toastkid.yobidashi4.presentation.component.HoverHighlightRow

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun InputHistoryView(tab: InputHistoryTab) {
    val viewModel = remember { InputHistoryViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Box {
            LazyColumn(
                state = viewModel.listState(),
                userScrollEnabled = true,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                items(viewModel.items(), key = InputHistory::hashCode) { item ->
                    HoverHighlightRow(
                        modifier = Modifier.animateItem()
                    ) { textColor ->
                        Text(
                            "x",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .size(44.dp)
                                .padding(4.dp)
                                .padding(start = 8.dp)
                                .clickable { viewModel.delete(item) }
                                .semantics { contentDescription = "Delete item ${item.word}" }
                        )
                        Column(modifier = Modifier
                            .combinedClickable(
                                enabled = true,
                                onClick = {
                                    viewModel.open(item)
                                },
                                onLongClick = {
                                    viewModel.openOnBackground(item)
                                }
                            )
                            .padding(start = 4.dp)
                        ) {
                            Text(item.word, color = textColor)
                            Text(
                                viewModel.dateTimeString(item),
                                color = textColor,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))
                        }
                    }
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(viewModel.listState()),
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
            )

            val coroutineScope = rememberCoroutineScope()
            DisposableEffect(tab) {
                viewModel.launch(coroutineScope, tab)

                onDispose {
                    viewModel.onDispose(tab)
                }
            }
        }
    }
}