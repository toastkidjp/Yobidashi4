/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf

class ReorderableTabRowViewModel {

    private val draggingIndex = mutableStateOf<Int?>(null)

    private val dragOffset = mutableStateOf(0f)

    private val tabWidthsPx = mutableStateMapOf<Int, Float>()

    private var totalDragX = 0f

    private var isDragStarted = false

    fun calculateForIndividualTab(index: Int): Pair<Boolean, Float> {
        val isDragging = draggingIndex.value == index

        val targetTranslationX = when {
            isDragging -> dragOffset.value
            draggingIndex.value != null -> {
                val dragged = draggingIndex.value!!
                val draggedWidth = tabWidthsPx[dragged] ?: 0f

                if (dragged < index) {
                    val threshold = (dragged until index).sumOf { (tabWidthsPx[it] ?: 0f).toDouble() }.toFloat() + (tabWidthsPx[index] ?: 0f) * 0.5f
                    when {
                        dragOffset.value > threshold && draggedWidth != 0f -> -draggedWidth
                        else -> 0f
                    }
                } else if (dragged > index) {
                    val threshold = (index + 1..dragged).sumOf { (tabWidthsPx[it] ?: 0f).toDouble() }.toFloat() + (tabWidthsPx[index] ?: 0f) * 0.5f
                    if (dragOffset.value < -threshold) draggedWidth else 0f
                } else {
                    0f
                }
            }
            else -> 0f
        }

        return isDragging to targetTranslationX
    }

    fun setTabWidth(index: Int, width: Float) {
        tabWidthsPx[index] = width
    }

    fun onPress(index: Int, dragAmount: Float, touchSlop: Float) {
        totalDragX += dragAmount

        if (!isDragStarted && kotlin.math.abs(totalDragX) > touchSlop) {
            isDragStarted = true
            draggingIndex.value = index
            dragOffset.value = totalDragX
        }

        dragOffset.value += dragAmount
    }

    fun isDragStarted() = isDragStarted

    fun clearDragState() {
        draggingIndex.value = null
        dragOffset.value = 0f
    }

    fun getDragState() = draggingIndex.value to dragOffset.value

    fun setDragState(index: Int, totalDragX: Float) {
        draggingIndex.value = index
        dragOffset.value = totalDragX
    }

    private infix fun Int.stroke(range: IntRange): IntProgression = this..range.last

    fun incrementDragOffset(dragAmount: Float) {
        dragOffset.value += dragAmount
    }

    fun tabWidth(index: Int) = tabWidthsPx[index]

}