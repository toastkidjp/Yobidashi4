/*
 * Copyright (c) 2022 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package jp.toastkid.yobidashi4.presentation.viewmodel.number

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import jp.toastkid.yobidashi4.domain.model.number.NumberBoard
import jp.toastkid.yobidashi4.domain.model.number.NumberPlaceGame
import jp.toastkid.yobidashi4.domain.repository.number.GameRepository
import jp.toastkid.yobidashi4.domain.service.number.GameFileProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NumberPlaceViewModel : KoinComponent {

    private val gameRepository: GameRepository by inject()

    private val _game = mutableStateOf(NumberPlaceGame())

    private val _mask = mutableStateOf(NumberBoard())

    private val _loading = mutableStateOf(false)

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

}