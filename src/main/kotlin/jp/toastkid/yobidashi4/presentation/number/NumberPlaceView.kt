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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.repository.number.GameRepository
import jp.toastkid.yobidashi4.domain.service.number.GameFileProvider
import jp.toastkid.yobidashi4.infrastructure.repository.number.GameRepositoryImplementation
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import jp.toastkid.yobidashi4.presentation.viewmodel.number.NumberPlaceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NumberPlaceView() {
    val fontSize = 32.sp

    val koin = object : KoinComponent {
        val setting: Setting by inject()
        val mainViewModel: MainViewModel by inject()
    }

    val setting = koin.setting
    val viewModel = remember { NumberPlaceViewModel() }
    val renewGame = {
        viewModel.initializeSolving()
        viewModel.initialize(setting.getMaskingCount())
        viewModel.saveCurrentGame()
    }
    LaunchedEffect(key1 = viewModel, block = {
        val file = GameFileProvider().invoke()
        if (file != null && Files.size(file) != 0L) {
            val game = GameRepositoryImplementation().load(file)
            if (game != null) {
                viewModel.setGame(game)
                return@LaunchedEffect
            }
        }
        withContext(Dispatchers.IO) {
            viewModel.initialize(setting.getMaskingCount())
            viewModel.saveCurrentGame()
        }
    })

    val numberStates = mutableListOf<MutableState<String>>()

    Surface(
        color = MaterialTheme.colors.surface.copy(0.5f),
        elevation = 4.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                AppBarContent(setting, fontSize, renewGame)

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
                                numberStates.add(number)

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
                                            showMessageSnackbar(koin.mainViewModel, done) {
                                                deleteGame()
                                                viewModel.initializeSolving()
                                                viewModel.initialize(setting.getMaskingCount())
                                                viewModel.saveCurrentGame()
                                            }
                                        }
                                    },
                                    fontSize,
                                    modifier = Modifier
                                        .weight(1f)
                                        .combinedClickable(
                                            onClick = {
                                                open.value = true
                                            },
                                            onLongClick = {
                                                koin.mainViewModel.showSnackbar(
                                                    "Would you like to use hint?",
                                                    "Use"
                                                ) {
                                                    viewModel.useHint(
                                                        rowIndex,
                                                        columnIndex,
                                                        number
                                                    ) { done ->
                                                        showMessageSnackbar(koin.mainViewModel, done)
                                                    }
                                                }
                                            }
                                        )
                                )
                            } else {
                                Text(
                                    cellValue.toString(),
                                    fontSize = fontSize,
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
    }

    /*
    OptionMenu(
            titleId = R.string.menu_other_board,
            action = {
                deleteCurrentGame(context)
                contentViewModel.nextRoute("tool/number/place")
            }),
        OptionMenu(
            titleId = R.string.menu_set_correct_answer,
            action = {
                viewModel.setCorrect()
            }),
        OptionMenu(
            titleId = R.string.clear_all,
            action = {
                viewModel.initializeSolving()
                numberStates.forEach { it.value = "_" }
            })
     */

    DisposableEffect(key1 = viewModel, effect = {
        onDispose {
            viewModel.saveCurrentGame()
        }
    })
}

private fun deleteGame() {
    val file = GameFileProvider().invoke()
    file?.let {
        object : KoinComponent{ val repo: GameRepository by inject() }.repo.delete(file)
    }
}

private fun showMessageSnackbar(
    mainViewModel: MainViewModel?,
    done: Boolean,
    onAction: () -> Unit = {}
) {
    mainViewModel?.showSnackbar(
        if (done) "Well done!" else "Incorrect...",
        if (done) "Next game" else ""
    ) {
        onAction()
    }
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
    setting: Setting,
    fontSize: TextUnit,
    renewGame: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val openMaskingCount = remember { mutableStateOf(false) }
        val maskingCount = remember { mutableStateOf("${setting.getMaskingCount()}") }

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
                    openMaskingCount.value = true
                }
        ) {
            Text(
                maskingCount.value,
                textAlign = TextAlign.Center,
                fontSize = fontSize
            )
            DropdownMenu(
                openMaskingCount.value,
                onDismissRequest = { openMaskingCount.value = false }) {
                (1..64).forEach {
                    DropdownMenuItem(
                        onClick = {
                        maskingCount.value = "$it"
                        openMaskingCount.value = false
                            setting.setMaskingCount(it)
                            deleteGame()
                        renewGame()
                        //contentViewModel?.nextRoute("tool/number/place")
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
