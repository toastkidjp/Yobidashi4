package jp.toastkid.yobidashi4.presentation.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.window.TrayState
import androidx.compose.ui.window.WindowState
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.service.notification.ScheduledNotification
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.main.title.LauncherJarTimestampReader
import jp.toastkid.yobidashi4.presentation.slideshow.viewmodel.SlideshowViewModel
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MainApplicationKtTest {

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var  setting: Setting

    @MockK
    private lateinit var notification: ScheduledNotification

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { mainViewModel.windowVisible() } returns false
        every { mainViewModel.darkMode() } returns false
        every { mainViewModel.loadBackgroundImage() } just Runs
        every { mainViewModel.windowState() } returns WindowState()
        every { mainViewModel.slideshowPath() } returns null
        every { mainViewModel.registerDroppedPathReceiver(any()) } just Runs
        coEvery { mainViewModel.launchDroppedPathFlow() } just Runs
        every { mainViewModel.trayState() } returns TrayState()
        coEvery { notification.start(any()) } just Runs
        every { notification.notificationFlow() } returns MutableSharedFlow()

        mockMainMenu(setting)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind (MainViewModel::class)
                    single(qualifier = null) { setting } bind (Setting::class)
                    single(qualifier = null) { notification } bind (ScheduledNotification::class)
                }
            )
        }
        mockkConstructor(LauncherJarTimestampReader::class)
        every { anyConstructed<LauncherJarTimestampReader>().invoke() } returns "test"
    }

    private fun mockMainMenu(setting: Setting) {
        every { mainViewModel.setShowInputBox(any()) } just Runs
        every { mainViewModel.edit(any()) } just Runs
        every { mainViewModel.openArticleList() } returns false
        every { mainViewModel.switchArticleList() } just Runs
        every { mainViewModel.showAggregationBox() } returns false
        every { mainViewModel.setInitialAggregationType(any()) } just Runs
        every { mainViewModel.switchAggregationBox(any()) } just Runs
        every { mainViewModel.openFile(any()) } just Runs
        every { mainViewModel.openTab(any()) } just Runs
        every { mainViewModel.openFileListTab(any(), any(), any(), any()) } just Runs
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
        every { mainViewModel.snackbarHostState() } returns SnackbarHostState()
        every { mainViewModel.showBackgroundImage() } returns false
        every { mainViewModel.showWebSearch() } returns false
        every { mainViewModel.showAggregationBox() } returns false
        every { mainViewModel.openFind() } returns false
        every { mainViewModel.openWorldTime() } returns false
        every { mainViewModel.showInputBox() } returns false
        every { mainViewModel.openMemoryUsageBox() } returns false
        every { mainViewModel.closeSlideshow() } just Runs
        every { mainViewModel.loadBackgroundImage() } just Runs
        every { mainViewModel.openArticleList() } returns false
        every { mainViewModel.articles() } returns emptyList()
        every { mainViewModel.reloadAllArticle() } just Runs
        every { mainViewModel.tabs } returns mutableListOf<Tab>()
        every { mainViewModel.selected } returns mutableStateOf(0)

        every { setting.articleFolderPath() } returns mockk()
        every { setting.userAgentName() } returns "test"
        every { setting.setUserAgentName(any()) } just Runs
        every { setting.save() } just Runs

        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class, ExperimentalFoundationApi::class)
    @Test
    fun test() {
        launchMainApplication(false)
    }

    @Test
    fun slideshowPath() {
        every { anyConstructed<LauncherJarTimestampReader>().invoke() } returns null
        every { mainViewModel.slideshowPath() } returns mockk()
        mockkConstructor(SlideshowViewModel::class)
        every { anyConstructed<SlideshowViewModel>().windowVisible() } returns false
        mockkStatic(Files::class)
        every { Files.lines(any()) } returns """
# Test
""".split("\n").stream()

        launchMainApplication(false)

        verify { anyConstructed<SlideshowViewModel>().windowVisible() }
    }

}