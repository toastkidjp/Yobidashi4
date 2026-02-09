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
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.flow.emptyFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import java.awt.Panel

class WebTabViewKtTest {

    @MockK
    private lateinit var webViewPool: WebViewPool

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { webViewPool } bind(WebViewPool::class)
                    single(qualifier = null) { mainViewModel } bind(MainViewModel::class)
                }
            )
        }

        every { webViewPool.find(any(), any(), any()) } just Runs
        every { webViewPool.reload(any()) } just Runs
        every { webViewPool.clearFind(any()) } just Runs
        every { webViewPool.component(any(), any()) } returns Panel()
        every { mainViewModel.showingSnackbar() } returns false
        every { mainViewModel.finderFlow() } returns emptyFlow()
        mockkConstructor(WebTabViewModel::class)
        coEvery { anyConstructed<WebTabViewModel>().start(any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
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
                    WebTabView(tabs[index.value])

                    index.value = 1
                }
            }

            verify(exactly = 2) { webViewPool.component(any(), any()) }
        }
    }

}