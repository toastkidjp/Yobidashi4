/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.PlatformContext.DefaultViewConfiguration.touchSlop
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@OptIn(InternalComposeUiApi::class)
@Composable
internal fun <T> ReorderableTabRow(
    tabs: List<T>,
    selectedTabIndex: Int,
    backgroundColor: Color,
    indicator: @Composable @UiComposable (tabPositions: List<TabPosition>) -> Unit = { tabPositions ->
        TabRowDefaults.Indicator(Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]))
    },
    onTabsReordered: (fromIndex: Int, toIndex: Int) -> Unit,
    content: @Composable (Int, T) -> Unit
) {
    val draggingIndex = remember { mutableStateOf<Int?>(null) }
    val dragOffset = remember { mutableStateOf(0f) }
    val tabWidthsPx = remember { mutableStateMapOf<Int, Float>() }

    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = backgroundColor,
        indicator = indicator,
        edgePadding = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        tabs.forEachIndexed { index, item ->
            val isDragging = draggingIndex.value == index

            val targetTranslationX = when {
                isDragging -> dragOffset.value
                draggingIndex.value != null -> {
                    val dragged = draggingIndex.value!!
                    val draggedWidth = tabWidthsPx[dragged] ?: 0f

                    if (dragged < index) {
                        val threshold = (dragged until index).sumOf { (tabWidthsPx[it] ?: 0f).toDouble() }.toFloat() + (tabWidthsPx[index] ?: 0f) * 0.5f
                        if (dragOffset.value > threshold) -draggedWidth else 0f
                    } else if (dragged > index) {
                        val threshold = (index + 1..dragged).sumOf { (tabWidthsPx[it] ?: 0f).toDouble() }.toFloat() + (tabWidthsPx[index] ?: 0f) * 0.5f
                        if (dragOffset.value < -threshold) draggedWidth else 0f
                    } else {
                        0f
                    }
                }
                else -> 0f
            }

            val animatedTranslationX = animateFloatAsState(targetValue = targetTranslationX)

            Box(
                modifier = Modifier
                    .zIndex(if (isDragging) 1f else 0f)
                    .onGloballyPositioned { coordinates ->
                        tabWidthsPx[index] = coordinates.size.width.toFloat()
                    }
                    .graphicsLayer {
                        translationX = animatedTranslationX.value
                    }
                    .shadow(if (isDragging) 8.dp else 0.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .pointerInput(tabs, selectedTabIndex) {
                        awaitPointerEventScope {
                            while (true) {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                var pointerId = down.id
                                var totalDragX = 0f
                                var isDragStarted = false

                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.firstOrNull { it.id == pointerId }
                                        ?: break

                                    if (change.pressed) {
                                        val dragAmount = change.positionChange().x
                                        totalDragX += dragAmount

                                        if (!isDragStarted) {
                                            if (kotlin.math.abs(totalDragX) > touchSlop) {
                                                isDragStarted = true
                                                draggingIndex.value = index
                                                dragOffset.value = totalDragX
                                                change.consume()
                                            }
                                        } else {
                                            dragOffset.value += dragAmount
                                            change.consume()
                                        }
                                    } else {
                                        if (!isDragStarted) {
                                            return@awaitPointerEventScope
                                        } else {
                                            val draggedIndex = draggingIndex.value
                                            val totalOffset = dragOffset.value

                                            if (draggedIndex != null) {
                                                val draggedWidth = tabWidthsPx[draggedIndex] ?: 1f
                                                var accumulated = 0f
                                                var targetIndex = draggedIndex

                                                if (totalOffset > 0) {
                                                    for (i in draggedIndex + 1 stroke tabs.indices) {
                                                        val w = tabWidthsPx[i] ?: draggedWidth
                                                        if (totalOffset > accumulated + w * 0.5f) {
                                                            targetIndex = i
                                                            accumulated += w
                                                        } else break
                                                    }
                                                } else if (totalOffset < 0) {
                                                    for (i in draggedIndex - 1 downTo 0) {
                                                        val w = tabWidthsPx[i] ?: draggedWidth
                                                        if (totalOffset < -accumulated - w * 0.5f) {
                                                            targetIndex = i
                                                            accumulated += w
                                                        } else break
                                                    }
                                                }

                                                targetIndex = targetIndex.coerceIn(0, tabs.lastIndex)
                                                if (targetIndex != draggedIndex) {
                                                    onTabsReordered(draggedIndex, targetIndex)
                                                }
                                            }
                                        }

                                        draggingIndex.value = null
                                        dragOffset.value = 0f
                                        break
                                    }
                                }
                            }
                        }
                    }
            ) {
                content(index, item)
            }
        }
    }
}

private infix fun Int.stroke(range: IntRange): IntProgression = this..range.last
