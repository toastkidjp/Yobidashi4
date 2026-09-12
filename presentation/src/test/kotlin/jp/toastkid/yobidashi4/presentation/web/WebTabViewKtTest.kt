/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.web

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WebTabViewKtTest {

    @RelaxedMockK
    private lateinit var viewModel: WebTabViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        coEvery { viewModel.start(any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun webTabView() {
        runDesktopComposeUiTest {
            setContent {
                val tabs = mutableListOf(
                    WebTab("test", "https://www.yahoo.com"),
                    WebTab("test2", "https://www.yahoo.co.jp")
                )

                val index = remember { mutableStateOf(0) }

                Window(
                    {},
                    visible = true,
                    state = rememberWindowState(width = 1.dp, height = 1.dp)
                ) {
                    WebTabView(tabs[index.value], viewModel)

                    index.value = 1
                }
            }
        }
    }

}