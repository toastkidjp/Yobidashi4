/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.menu

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import androidx.compose.ui.window.Window
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.service.article.finder.AsynchronousArticleIndexerService
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_left_panel_close
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MainMenuKtTest {

    @RelaxedMockK
    private lateinit var viewModel: MainMenuViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var asynchronousArticleIndexerService: AsynchronousArticleIndexerService

    @MockK
    private lateinit var  setting: Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier=null) { asynchronousArticleIndexerService } bind(AsynchronousArticleIndexerService::class)
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }

        every { mainViewModel.setShowInputBox(any()) } just Runs
        every { mainViewModel.edit(any()) } just Runs
        every { mainViewModel.openArticleList() } returns false
        every { mainViewModel.switchArticleList() } just Runs
        every { mainViewModel.showAggregationBox() } returns false
        every { mainViewModel.setInitialAggregationType(any()) } just Runs
        every { mainViewModel.switchAggregationBox(any()) } just Runs
        every { mainViewModel.openFile(any()) } just Runs
        every { mainViewModel.openTab(any()) } just Runs
        every { mainViewModel.openFileListTab(any(), any(), any()) } just Runs
        every { mainViewModel.openUrl(any(), any()) } just Runs
        every { mainViewModel.openTextFile(any()) } just Runs
        val tab = mockk<Tab>()
        every { mainViewModel.currentTab() } returns tab
        every { tab.title() } returns "test"
        every { mainViewModel.saveCurrentEditorTab() } just Runs
        every { mainViewModel.switchFind() } just Runs
        every { mainViewModel.switchUseBackground() } just Runs
        every { mainViewModel.switchDarkMode() } just Runs
        every { mainViewModel.switchMemoryUsageBox() } just Runs
        every { mainViewModel.toggleFullscreen() } just Runs
        every { mainViewModel.toggleNarrowWindow() } just Runs
        every { mainViewModel.toggleFullscreenLabel() } returns "test"
        every { mainViewModel.closeCurrent() } just Runs
        every { mainViewModel.closeAllTabs() } just Runs
        every { mainViewModel.closeOtherTabs() } just Runs
        every { mainViewModel.closeSlideshow() } just Runs
        every { mainViewModel.showSnackbar(any(), any(), any()) } just Runs
        every { mainViewModel.tabs } returns mutableListOf()
        every { mainViewModel.slideshow(any()) } just Runs
        every { mainViewModel.setSelectedIndex(any()) } just Runs
        every { mainViewModel.openMemoryUsageBox() } returns false
        every { mainViewModel.loadBackgroundImage() } just Runs
        every { mainViewModel.setShowWebSearch(any()) } just Runs
        every { mainViewModel.setInitialAggregationType(any()) } just Runs
        every { mainViewModel.openWorldTime() } returns true

        every { setting.articleFolderPath() } returns mockk()
        every { setting.userAgentName() } returns "test"
        every { setting.setUserAgentName(any()) } just Runs
        every { setting.save() } just Runs

        every { viewModel.useEditorMenu() } returns false
        every { viewModel.switchArticleListIconPath() } returns Res.drawable.ic_left_panel_close

        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class)
    @Test
    fun test() {
        runDesktopComposeUiTest {
            setContent {
                Window({}, visible = false) {
                    MainMenu({}, viewModel)
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class)
    @Test
    fun currentIsEditorTab() {
        every { viewModel.useEditorMenu() } returns true

        runDesktopComposeUiTest {
            setContent {
                Window({}, visible = false) {
                    MainMenu({}, viewModel)
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class)
    @Test
    fun currentIsWebTab() {
        val tab = mockk<WebTab>()
        every { mainViewModel.currentTab() } returns tab

        runDesktopComposeUiTest {
            setContent {
                Window({}, visible = false) {
                    MainMenu({}, viewModel)
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class)
    @Test
    fun currentIsMarkdownPreviewTab() {
        every { viewModel.useEditorMenu() } returns false

        runDesktopComposeUiTest {
            setContent {
                Window({}, visible = false) {
                    MainMenu({}, viewModel)
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class)
    @Test
    fun useAdditionalTabMenu() {
        every { viewModel.useAdditionalTabMenu() } returns true
        every { viewModel.currentIsWebTab() } returns true

        runDesktopComposeUiTest {
            setContent {
                Window({}, visible = false) {
                    MainMenu({  }, viewModel)
                }
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun canMoveTab() {
        every { viewModel.canMoveTab() } returns true

        runDesktopComposeUiTest {
            setContent {
                Window({}, visible = false) {
                    MainMenu({}, viewModel)
                }
            }
        }
    }

}