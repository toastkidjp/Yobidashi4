/*
 * Copyright (c) 2022 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package jp.toastkid.yobidashi4.presentation.number

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
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
                AppBarContent(viewModel, { viewModel.deleteGame() }, viewModel::renewGame, viewModel.getMaskingCount(), { viewModel.setMaskingCount(it) }, viewModel.openingMaskingCount(), { viewModel.openMaskingCount() }, { viewModel.closeMaskingCount() })

                HorizontalDivider(0)

                viewModel.masked().rows().forEachIndexed { rowIndex, row ->
                    Row(
                        modifier = Modifier.height(IntrinsicSize.Min)
                    ) {
                        VerticalDivider(0)

                        row.forEachIndexed { columnIndex, cellValue ->
                            if (cellValue == -1) {
                                val open = remember { mutableStateOf(false) }
                                val number = remember { mutableStateOf("_") }
                                viewModel.addNumber(number)

                                val solving = viewModel.pickSolving(rowIndex, columnIndex)
                                if (solving != -1) {
                                    number.value = "$solving"
                                }

                                MaskedCell(
                                    open,
                                    number,
                                    {
                                        number.value = "$it"
                                        open.value = false
                                        viewModel.place(rowIndex, columnIndex, it) { done ->
                                            viewModel.showMessageSnackbar(done) {
                                                viewModel.startNewGame()
                                            }
                                        }
                                    },
                                    viewModel.fontSize(),
                                    modifier = Modifier
                                        .weight(1f)
                                        .combinedClickable(
                                            onClick = {
                                                open.value = true
                                            },
                                            onLongClick = {
                                                viewModel.onCellLongClick(rowIndex, columnIndex, number)
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

                            VerticalDivider(columnIndex)
                        }
                    }
                    HorizontalDivider(rowIndex)
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
                onClick = {
                    viewModel.clear()
                }
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
private fun VerticalDivider(columnIndex: Int) {
    Divider(
        modifier = Modifier
            .fillMaxHeight()
            .width(calculateThickness(columnIndex))
    )
}

@Composable
private fun HorizontalDivider(rowIndex: Int) {
    Divider(thickness = calculateThickness(rowIndex))
}

private fun calculateThickness(columnIndex: Int) = if (columnIndex % 3 == 2) 2.dp else 1.dp

@Composable
private fun AppBarContent(
    viewModel: NumberPlaceViewModel,
    deleteGame: () -> Unit,
    renewGame: () -> Unit,
    maskingCount: Int,
    setMaskingCount: (Int) -> Unit,
    openingMaskingCount: Boolean,
    openMaskingCount: () -> Unit,
    closeMaskingCount: () -> Unit
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
                .clickable {
                    openMaskingCount()
                }
        ) {
            Text(
                "$maskingCount",
                textAlign = TextAlign.Center,
                fontSize = viewModel.fontSize()
            )
            DropdownMenu(
                openingMaskingCount,
                onDismissRequest = { closeMaskingCount() }) {
                (1..64).forEach {
                    DropdownMenuItem(
                        onClick = {
                            setMaskingCount(it)
                        closeMaskingCount()
                            viewModel.setMaskingCount(it)
                            deleteGame()
                        renewGame()
                        //contentViewModel?.nextRoute("tool/number/place")
                    }) {
                        Text(
                            text = "$it",
                            fontSize = viewModel.fontSize(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MaskedCell(
    openState: MutableState<Boolean>,
    numberState: MutableState<String>,
    onMenuItemClick: (Int) -> Unit,
    fontSize: TextUnit,
    modifier: Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(
            numberState.value,
            color = Color(0xFFAA99FF),
            fontSize = fontSize,
            textAlign = TextAlign.Center
        )
        DropdownMenu(openState.value, onDismissRequest = { openState.value = false }) {
            (1..9).forEach {
                DropdownMenuItem(
                    onClick = {
                    onMenuItemClick(it)
                }) {
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
