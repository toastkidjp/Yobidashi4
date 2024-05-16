/*
 * Copyright (c) 2022 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package jp.toastkid.yobidashi4.presentation.number

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.number.NumberBoard
import jp.toastkid.yobidashi4.domain.model.number.NumberPlaceGame
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.repository.number.GameRepository
import jp.toastkid.yobidashi4.domain.service.number.GameFileProvider
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NumberPlaceViewModel : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    private val setting: Setting by inject()

    private val gameRepository: GameRepository by inject()

    private val _game = mutableStateOf(NumberPlaceGame())

    private val _mask = mutableStateOf(NumberBoard())

    private val _loading = mutableStateOf(false)

    private val numberStates = mutableStateMapOf<String, CellState>()

    private val openOption = mutableStateOf(false)

    private val openMaskingCount = mutableStateOf(false)

    private val maskingCount = mutableStateOf("")

    fun initialize(maskingCount: Int) {
        _loading.value = true
        _game.value.initialize(maskingCount)
        _mask.value = _game.value.masked()
        _loading.value = false
    }

    fun initializeSolving() {
        _loading.value = true
        _game.value.initializeSolving()
        _mask.value = _game.value.masked()
        numberStates.keys.forEach { numberStates.put(it, CellState()) }
        _loading.value = false
        closeDropdown()
    }

    fun setGame(game: NumberPlaceGame) {
        _loading.value = true
        _game.value = game
        _mask.value = _game.value.masked()
        walkMatrix(_game.value.masked().rows(), ::setSolving)
        _loading.value = false
    }

    private fun walkMatrix(matrix: List<List<Int>>, biConsumer: (Int, Int) -> Unit) {
        matrix.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, i ->
                biConsumer(rowIndex, columnIndex)
            }
        }
    }

    fun setCorrect() {
        _loading.value = true
        _game.value.setCorrect()
        _mask.value = _game.value.masked()
        _loading.value = false
        closeDropdown()
    }

    fun masked() = _mask.value

    fun loading(): Boolean = _loading.value

    fun place(rowIndex: Int, columnIndex: Int, it: Int) {
        _game.value.place(rowIndex, columnIndex, it) { done ->
            showMessageSnackbar(done, ::startNewGame)
        }
        numberStates.put("${rowIndex}-${columnIndex}", CellState(it))
    }

    fun useHint(
        rowIndex: Int,
        columnIndex: Int,
        onSolved: (Boolean) -> Unit
    ) {
        val it = _game.value.pickCorrect(rowIndex, columnIndex)
        _game.value.place(rowIndex, columnIndex, it, onSolved)
        numberStates.put("${rowIndex}-${columnIndex}", CellState(it))
    }

    fun saveCurrentGame() {
        val file = GameFileProvider().invoke() ?: return
        gameRepository.save(file, _game.value)
    }

    private fun pickSolving(rowIndex: Int, columnIndex: Int): Int {
        return _game.value.pickSolving(rowIndex, columnIndex)
    }

    private fun setSolving(rowIndex: Int, columnIndex: Int) {
        val solving = pickSolving(rowIndex, columnIndex)
        numberStates.put("${rowIndex}-${columnIndex}", CellState(solving))
    }

    fun openingCellOption(rowIndex: Int, columnIndex: Int): Boolean {
        val state = numberStates.get("${rowIndex}-${columnIndex}") ?: return false
        return state.open
    }

    fun openCellOption(rowIndex: Int, columnIndex: Int) {
        val state = numberStates.get("${rowIndex}-${columnIndex}") ?: return
        numberStates.put("${rowIndex}-${columnIndex}", state.copy(open = true))
    }

    fun closeCellOption(rowIndex: Int, columnIndex: Int) {
        val state = numberStates.get("${rowIndex}-${columnIndex}") ?: return
        numberStates.put("${rowIndex}-${columnIndex}", state.copy(open = false))
    }

    fun numberLabel(rowIndex: Int, columnIndex: Int): String {
        val state = numberStates.get("${rowIndex}-${columnIndex}") ?: return ""
        return state.text()
    }

    fun deleteGame() {
        val file = GameFileProvider().invoke()
        file?.let {
            gameRepository.delete(file)
        }
    }

    fun renewGame() {
        val game = NumberPlaceGame()
        game.initialize(setting.getMaskingCount())
        setGame(game)
        initializeSolving()
        initialize(setting.getMaskingCount())
        saveCurrentGame()
        closeDropdown()
    }

    fun reloadGame() {
        deleteGame()
        renewGame()
    }

    fun startNewGame() {
        deleteGame()
        initializeSolving()
        initialize(setting.getMaskingCount())
        saveCurrentGame()
    }

    suspend fun start() {
        val file = GameFileProvider().invoke()
        if (file != null && Files.size(file) != 0L) {
            val game = gameRepository.load(file)
            if (game != null) {
                setGame(game)
                return
            }
        }

        withContext(Dispatchers.IO) {
            initialize(setting.getMaskingCount())
            saveCurrentGame()
            maskingCount.value = "${getMaskingCount()}"
        }
    }

    fun showMessageSnackbar(
        done: Boolean,
        onAction: () -> Unit = {}
    ) {
        mainViewModel.showSnackbar(
            if (done) "Well done!" else "Incorrect...",
            if (done) "Next game" else ""
        ) {
            onAction()
        }
    }

    fun fontSize() = 32.sp

    fun getMaskingCount(): Int = setting.getMaskingCount()

    fun setMaskingCount(it: Int) {
        setting.setMaskingCount(it)
    }

    fun onCellLongClick(rowIndex: Int, columnIndex: Int) {
        mainViewModel.showSnackbar(
            "Would you like to use hint?",
            "Use"
        ) {
            useHint(
                rowIndex,
                columnIndex
            ) { done ->
                showMessageSnackbar(done)
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun onPointerEvent(awaitPointerEvent: PointerEvent) {
        if (awaitPointerEvent.type == PointerEventType.Press
            && !openOption.value
            && awaitPointerEvent.button == PointerButton.Secondary
        ) {
            openOption.value = true
        }
    }

    fun openingDropdown() = openOption.value

    fun closeDropdown() {
        openOption.value = false
    }

    fun clear() {
        initializeSolving()
        numberStates.keys.forEach { numberStates.put(it, CellState()) }
    }

    fun openingMaskingCount() = openMaskingCount.value

    fun openMaskingCount() {
        openMaskingCount.value = true
    }

    fun closeMaskingCount() {
        openMaskingCount.value = false
    }

    fun calculateThickness(columnIndex: Int) = if (columnIndex % 3 == 2) 2.dp else 1.dp

}