/*
 * Copyright (c) 2022 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package jp.toastkid.yobidashi4.domain.model.number

import kotlinx.serialization.Serializable
import java.util.LinkedList
import java.util.Queue
import java.util.Random

@Serializable
data class NumberBoard(
    private val rows: MutableList<MutableList<Int>> = mutableListOf()
) {

    fun fillZero() {
        rows.clear()

        (0 until BOARD_SIZE).forEach { x ->
            rows.add(mutableListOf())
            repeat((0 until BOARD_SIZE).count()) {
                rows[x].add(0)
            }
        }
    }

    fun placeRandom() {
        iterate { x, y ->
            if (rows[y][x] == 0) {
                val boxNumbers = getIntInBox(x, y)
                val verticalNumbers = getIntVertical(x)
                val horizontalNumbers = rows[y]
                val next = makeRandomWithout(
                    boxNumbers.union(verticalNumbers).union(horizontalNumbers)
                )
                rows[y][x] = next
            }
        }
    }

    private fun makeRandomWithout(existsNumbers: Set<Int>): Int {
        val uniqueQueue = makeRandomUniqueQueue(BOARD_SIZE)
        var n = uniqueQueue.poll()
        while (existsNumbers.contains(n)) {
            n = uniqueQueue.poll()
        }
        return n ?: -1
    }

    private fun getIntVertical(x: Int): Set<Int> {
        val currentNumbers = mutableSetOf<Int>()
        (0 until BOARD_SIZE).forEach { y ->
            currentNumbers.add(rows[y][x])
        }

        return currentNumbers
    }

    private fun getIntInBox(parameterX: Int, parameterY: Int): Set<Int> {
        val currentNumbers = mutableSetOf<Int>()
        (initialIndex(parameterX) .. initialIndex(parameterX) + 2).forEach { x ->
            (initialIndex(parameterY)..initialIndex(parameterY) + 2).forEach { y ->
                currentNumbers.add(rows[y][x])
            }
        }

        return currentNumbers
    }

    private fun initialIndex(n: Int) =
        when (n) {
            in (3..5) -> 3
            in (6..8) -> 6
            else -> 0
        }

    private fun makeRandomUniqueQueue(size: Int): Queue<Int> {
        val queue = LinkedList<Int>()
        queue.addAll((1 .. size).shuffled())
        return queue
    }

    fun masked(maskNumberCount: Int): NumberBoard {
        val newBoard = makeWithZero()
        newBoard.copyFrom(this)
        val random = Random()
        val randomPair = mutableSetOf<String>()

        while (randomPair.size < maskNumberCount) {
            randomPair.add("${random.nextInt(9)}_${random.nextInt(9)}")
        }
        randomPair.map {
            val split = it.split("_")
            split[0].toInt() to split[1].toInt()
        }.forEach {
            newBoard.rows[it.first][it.second] = -1
        }
        return newBoard
    }

    fun copyFrom(base: NumberBoard) {
        iterate { x, y ->
            this.rows[x][y] = base.rows[x][y]
        }
    }

    fun rows(): List<List<Int>> {
        return rows
    }

    fun isCorrect(correct: NumberBoard?): Boolean {
        if (correct !is NumberBoard) {
            return false
        }

        this.rows.forEachIndexed { rowNumber, row ->
            correct.rows[rowNumber].forEachIndexed { columnIndex, column ->
                if (column != row.get(columnIndex)) {
                    return false
                }
            }
        }

        return true
    }

    fun place(rowIndex: Int, columnIndex: Int, number: Int) {
        rows[rowIndex][columnIndex] = number
    }

    fun fulfilled(): Boolean {
        return this.rows.none { it.contains(-1) }
    }

    fun pick(rowIndex: Int, columnIndex: Int): Int {
        return this.rows[rowIndex][columnIndex]
    }

    private fun iterate(action: (Int, Int) -> Unit) {
        (0 until BOARD_SIZE).forEach { x ->
            (0 until BOARD_SIZE).forEach { y ->
                action(x, y)
            }
        }
    }

    companion object {

        private const val BOARD_SIZE = 9

        fun make(): NumberBoard {
            val numberBoard = makeWithZero()
            numberBoard.placeRandom()
            return numberBoard
        }

        fun makeWithZero(): NumberBoard {
            val newBoard = NumberBoard()
            newBoard.fillZero()
            return newBoard
        }

    }

}