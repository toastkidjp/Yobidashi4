/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ReorderableTabRowTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testRightTriggersCallback() {
        val tabs = listOf("Tab A", "Tab B", "Tab C")
        var reorderedResult: Pair<Int, Int>? = null

        runComposeUiTest {
            setContent {
                ReorderableTabRow(
                    tabs = tabs,
                    selectedTabIndex = 0,
                    backgroundColor = Color.White,
                    indicator = { _ -> },
                    onTabsReordered = { from, to ->
                        reorderedResult = Pair(from, to)
                    }
                ) { index, item ->
                    Text(
                        text = item,
                        modifier = Modifier
                            .padding(16.dp)
                            .semantics { contentDescription = "Tab_$index" }
                    )
                }
            }

            onNodeWithContentDescription("Tab_0").performTouchInput {
                swipe(
                    start = center,
                    end = center.copy(x = center.x + 300f),
                    durationMillis = 300
                )
            }

            onNodeWithContentDescription("Tab_0").performTouchInput {
                swipe(
                    start = center,
                    end = center.copy(x = center.x + 300f),
                    durationMillis = 300
                )
            }

            assertNotNull(reorderedResult)
            assertEquals(0, reorderedResult?.first)
            val targetIndex = reorderedResult?.second ?: 0
            assert(targetIndex > 0) { "Target index ($targetIndex) should be greater than 0" }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testLeftTriggersCallback() {
        val tabs = listOf("Tab A", "Tab B", "Tab C")
        var reorderedResult: Pair<Int, Int>? = null

        runComposeUiTest {
            setContent {
                ReorderableTabRow(
                    tabs = tabs,
                    selectedTabIndex = 0,
                    backgroundColor = Color.White,
                    indicator = { _ -> },
                    onTabsReordered = { from, to ->
                        reorderedResult = Pair(from, to)
                    }
                ) { index, item ->
                    Text(
                        text = item,
                        modifier = Modifier
                            .padding(16.dp)
                            .semantics { contentDescription = "Tab_$index" }
                    )
                }
            }

            onNodeWithContentDescription("Tab_1").performTouchInput {
                swipe(
                    start = center,
                    end = center.copy(x = center.x - 300f),
                    durationMillis = 300
                )
            }

            assertNotNull(reorderedResult)
            assertEquals(1, reorderedResult?.first)
            val targetIndex = reorderedResult?.second ?: 1
            assert(targetIndex < 1) { "Target index ($targetIndex) should be less than 1" }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testReturnsEarly() {
        val tabs = listOf("Tab A", "Tab B", "Tab C")
        var reorderedCalled = false

        runComposeUiTest {
            setContent {
                ReorderableTabRow(
                    tabs = tabs,
                    selectedTabIndex = 0,
                    backgroundColor = Color.White,
                    indicator = { _ -> },
                    onTabsReordered = { _, to ->
                        reorderedCalled = true
                    }
                ) { index, item ->
                    Text(
                        text = item,
                        modifier = Modifier
                            .padding(16.dp)
                            .semantics { contentDescription = "Tab_$index" }
                    )
                }
            }

            onNodeWithContentDescription("Tab_0").performTouchInput {
                down(center)
                up()
            }

            assert(!reorderedCalled) { "onTabsReordered should not be called on simple tap without drag" }
        }
    }

}
