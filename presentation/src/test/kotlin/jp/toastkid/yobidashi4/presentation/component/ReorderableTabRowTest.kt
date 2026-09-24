/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ReorderableTabRowTest {

    @BeforeEach
    fun setUp() {

    }

    @AfterEach
    fun tearDown() {

    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun test() {
        runComposeUiTest {
            setContent {
                ReorderableTabRow(
                    listOf("test", "test2"),
                    0,
                    Color.Red,
                    {},
                    { _, _ ->}
                ) { _, _ ->

                }
            }
        }
    }

}