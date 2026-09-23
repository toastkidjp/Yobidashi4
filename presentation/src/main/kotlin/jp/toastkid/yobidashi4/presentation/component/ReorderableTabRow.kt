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
import jp.toastkid.yobidashi4.presentation.lib.annotation.ExcludeCoverageCalculation

@ExcludeCoverageCalculation
@OptIn(InternalComposeUiApi::class)
@Composable
internal fun <T> ReorderableTabRow(
    tabs: List<T>,
    selectedTabIndex: Int,
    backgroundColor: Color,
    onTabsReordered: (fromIndex: Int, toIndex: Int) -> Unit,
    content: @Composable (Int, T) -> Unit
) {
    ReorderableTabRow(
        tabs,
        selectedTabIndex,
        backgroundColor,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]))
        },
        onTabsReordered,
        content
    )
}

@OptIn(InternalComposeUiApi::class)
@Composable
internal fun <T> ReorderableTabRow(
    tabs: List<T>,
    selectedTabIndex: Int,
    backgroundColor: Color,
    indicator: @Composable @UiComposable (tabPositions: List<TabPosition>) -> Unit,
    onTabsReordered: (fromIndex: Int, toIndex: Int) -> Unit,
    content: @Composable (Int, T) -> Unit
) {
    val viewModel = remember { ReorderableTabRowViewModel() }

    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = backgroundColor,
        indicator = indicator,
        edgePadding = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        tabs.forEachIndexed { index, item ->
            val (isDragging, targetTranslationX) = viewModel.calculateForIndividualTab(index)

            val animatedTranslationX = animateFloatAsState(targetValue = targetTranslationX)

            Box(
                modifier = Modifier
                    .zIndex(if (isDragging) 1f else 0f)
                    .onGloballyPositioned { coordinates ->
                        viewModel.setTabWidth(index, coordinates.size.width.toFloat())
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
                                                viewModel.setDragState(index, totalDragX)
                                                change.consume()
                                            }
                                        } else {
                                            viewModel.incrementDragOffset(dragAmount)
                                            change.consume()
                                        }

                                        continue
                                    }

                                    if (!isDragStarted) {
                                        return@awaitPointerEventScope
                                    }

                                    val (draggedIndex, totalOffset) = viewModel.getDragState()

                                    if (draggedIndex != null) {
                                        val draggedWidth = viewModel.tabWidth(draggedIndex) ?: 1f
                                        var accumulated = 0f
                                        var targetIndex = draggedIndex

                                        if (totalOffset > 0) {
                                            for (i in draggedIndex + 1 stroke tabs.indices) {
                                                val w = viewModel.tabWidth(i) ?: draggedWidth
                                                if (totalOffset > accumulated + w * 0.5f) {
                                                    targetIndex = i
                                                    accumulated += w
                                                } else break
                                            }
                                        } else if (totalOffset < 0) {
                                            for (i in draggedIndex - 1 downTo 0) {
                                                val w = viewModel.tabWidth(i) ?: draggedWidth
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

                                    viewModel.clearDragState()
                                    break
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
