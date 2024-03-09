/*
 * Copyright (c) 2022 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package jp.toastkid.yobidashi4.presentation.number

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.presentation.component.VerticalDivider

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NumberPlaceView() {
    val viewModel = remember { NumberPlaceViewModel() }

    LaunchedEffect(key1 = viewModel, block = {
        viewModel.start()
    })

    Surface(
        color = MaterialTheme.colors.surface.copy(0.5f),
        elevation = 4.dp,
        modifier = Modifier.pointerInput(Unit) {
            awaitEachGesture {
                viewModel.onPointerEvent(awaitPointerEvent())
            }
        }
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                AppBarContent(
                    viewModel::deleteGame,
                    viewModel::renewGame,
                    viewModel.getMaskingCount(),
                    viewModel::setMaskingCount,
                    viewModel.openingMaskingCount(),
                    viewModel::openMaskingCount,
                    viewModel::closeMaskingCount,
                    viewModel.fontSize()
                )

                Divider(thickness = viewModel.calculateThickness(0))

                viewModel.masked().rows().forEachIndexed { rowIndex, row ->
                    Row(
                        modifier = Modifier.height(IntrinsicSize.Min)
                    ) {
                        VerticalDivider(thickness = viewModel.calculateThickness(0), modifier = Modifier.height(44.dp))

                        row.forEachIndexed { columnIndex, cellValue ->
                            if (cellValue == -1) {
                                MaskedCell(
                                    viewModel.openingCellOption(rowIndex, columnIndex),
                                    { viewModel.closeCellOption(rowIndex, columnIndex) },
                                    viewModel.numberLabel(rowIndex, columnIndex),
                                    {
                                        viewModel.place(rowIndex, columnIndex, it)
                                    },
                                    viewModel.fontSize(),
                                    modifier = Modifier
                                        .weight(1f)
                                        .combinedClickable(
                                            onClick = {
                                                viewModel.openCellOption(rowIndex, columnIndex)
                                            },
                                            onLongClick = {
                                                viewModel.onCellLongClick(rowIndex, columnIndex)
                                            }
                                        )
                                )
                            } else {
                                Text(
                                    cellValue.toString(),
                                    fontSize = viewModel.fontSize(),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            VerticalDivider(thickness = viewModel.calculateThickness(columnIndex), modifier = Modifier.height(44.dp))
                        }
                    }
                    Divider(thickness = viewModel.calculateThickness(rowIndex))
                }
            }

            if (viewModel.loading().value) {
                CircularProgressIndicator()
            }
        }
        DropdownMenu(
            viewModel.openingDropdown(),
            onDismissRequest = viewModel::closeDropdown
        ) {
            DropdownMenuItem(
                onClick = viewModel::renewGame
            ) {
                Text("Other board")
            }

            DropdownMenuItem(
                onClick = viewModel::setCorrect
            ) {
                Text("Set answer")
            }

            DropdownMenuItem(
                onClick = viewModel::clear
            ) {
                Text("Clear")
            }
        }
    }

    DisposableEffect(key1 = viewModel, effect = {
        onDispose {
            viewModel.saveCurrentGame()
        }
    })
}

@Composable
private fun AppBarContent(
    deleteGame: () -> Unit,
    renewGame: () -> Unit,
    maskingCount: Int,
    setMaskingCount: (Int) -> Unit,
    openingMaskingCount: Boolean,
    openMaskingCount: () -> Unit,
    closeMaskingCount: () -> Unit,
    fontSize: TextUnit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(
            onClick = {
                deleteGame()
                renewGame()
            },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text("Reload")
        }

        Text(
            "Masking count: ",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 8.dp)
        )

        Box(
            modifier = Modifier
                .padding(start = 4.dp)
                .clickable(onClick = openMaskingCount)
        ) {
            Text(
                "$maskingCount",
                textAlign = TextAlign.Center,
                fontSize = fontSize
            )
            DropdownMenu(
                openingMaskingCount,
                onDismissRequest = closeMaskingCount) {
                val lazyListState = rememberLazyListState()
                Box(modifier = Modifier.width(100.dp).height(600.dp)) {
                    LazyColumn(state = lazyListState) {
                        items(64) {
                            val count = it + 1
                            DropdownMenuItem(
                                onClick = {
                                    setMaskingCount(count)
                                    closeMaskingCount()
                                    deleteGame()
                                    renewGame()
                                    //contentViewModel?.nextRoute("tool/number/place")
                                }) {
                                Text(
                                    text = "$count",
                                    fontSize = fontSize,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    VerticalScrollbar(
                        adapter = rememberScrollbarAdapter(lazyListState),
                        modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}

@Composable
private fun MaskedCell(
    open: Boolean,
    close: () -> Unit,
    numberLabel: String,
    onMenuItemClick: (Int) -> Unit,
    fontSize: TextUnit,
    modifier: Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(
            numberLabel,
            color = Color(0xFFAA99FF),
            fontSize = fontSize,
            textAlign = TextAlign.Center
        )
        DropdownMenu(open, onDismissRequest = close) {
            (1..9).forEach {
                DropdownMenuItem(onClick = { onMenuItemClick(it) }) {
                    Text(
                        text = "$it",
                        fontSize = fontSize,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
