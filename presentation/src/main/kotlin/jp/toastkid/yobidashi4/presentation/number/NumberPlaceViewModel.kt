/*
 * Copyright (c) 2022 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package jp.toastkid.yobidashi4.presentation.number

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
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

    private val numberStates = mutableListOf<MutableState<String>>()

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
        _loading.value = false
        closeDropdown()
    }

    fun setGame(game: NumberPlaceGame) {
        _loading.value = true
        _game.value = game
        _mask.value = _game.value.masked()
        _loading.value = false
    }

    fun setCorrect() {
        _loading.value = true
        _game.value.setCorrect()
        _mask.value = _game.value.masked()
        _loading.value = false
        closeDropdown()
    }

    fun masked() = _mask.value

    fun loading(): State<Boolean> = _loading

    fun place(rowIndex: Int, columnIndex: Int, it: Int, onSolved: (Boolean) -> Unit) {
        _game.value.place(rowIndex, columnIndex, it, onSolved)
    }

    fun useHint(
        rowIndex: Int,
        columnIndex: Int,
        numberState: MutableState<String>,
        onSolved: (Boolean) -> Unit
    ) {
        val it = _game.value.pickCorrect(rowIndex, columnIndex)
        numberState.value = "$it"
        _game.value.place(rowIndex, columnIndex, it, onSolved)
    }

    fun saveCurrentGame() {
        val file = GameFileProvider().invoke() ?: return
        gameRepository.save(file, _game.value)
    }

    fun pickSolving(rowIndex: Int, columnIndex: Int): Int {
        return _game.value.pickSolving(rowIndex, columnIndex)
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

    fun onCellLongClick(rowIndex: Int, columnIndex: Int, number: MutableState<String>) {
        mainViewModel.showSnackbar(
            "Would you like to use hint?",
            "Use"
        ) {
            useHint(
                rowIndex,
                columnIndex,
                number
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

    fun openDropdown() {
        openOption.value = true
    }

    fun closeDropdown() {
        openOption.value = false
    }

    fun addNumber(number: MutableState<String>) {
        numberStates.add(number)
    }

    fun clear() {
        initializeSolving()
        numberStates.forEach { it.value = "_" }
    }

    fun openingMaskingCount() = openMaskingCount.value

    fun openMaskingCount() {
        openMaskingCount.value = true
    }

    fun closeMaskingCount() {
        openMaskingCount.value = false
    }

}