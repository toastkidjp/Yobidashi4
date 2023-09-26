package jp.toastkid.yobidashi4.infrastructure.service.web

import java.awt.Point
import java.awt.Robot
import java.awt.event.KeyEvent
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import javax.imageio.ImageIO
import javax.swing.SwingUtilities
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import jp.toastkid.yobidashi4.presentation.viewmodel.web.WebTabViewModel
import org.cef.browser.CefBrowser
import org.cef.handler.CefKeyboardHandler
import org.cef.misc.EventFlags
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CefKeyboardShortcutProcessor(
    private val search: (String) -> Unit,
    private val selectedText: () -> String,
    private val browsePage: (String) -> Unit
) : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val webTabViewModel : WebTabViewModel by inject()

    operator fun invoke(browser: CefBrowser?, event: CefKeyboardHandler.CefKeyEvent): Boolean {
        if (event.type != CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_KEYUP) {
            return false
        }

        if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == KeyEvent.VK_F) {
            object : KoinComponent { val viewModel: WebTabViewModel by inject() }.viewModel.switchFind()
            return true
        }
        if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == KeyEvent.VK_W) {
            viewModel.closeCurrent()
            return true
        }
        if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == KeyEvent.VK_P) {
            browser?.let {
                it.printToPDF("${it.identifier}.pdf", null, null)
            }
            return true
        }
        if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == KeyEvent.VK_UP) {
            browser?.executeJavaScript("window.scrollTo(0, 0);", null, 1)
            return true
        }
        if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == KeyEvent.VK_DOWN) {
            browser?.executeJavaScript("window.scrollTo(0, document.body.scrollHeight);", null, 1)
            return true
        }
        if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == KeyEvent.VK_B) {
            BookmarkInsertion()(null, browser?.url)
            return true
        }
        if (event.windows_key_code == KeyEvent.VK_F5) {
            browser?.reload()
            return true
        }
        if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == KeyEvent.VK_R) {
            browser?.reloadIgnoreCache()
            return true
        }
        if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == 187) {
            browser?.let {
                it.zoomLevel = it.zoomLevel + 0.25
            }
            return true
        }
        if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN && event.windows_key_code == 189) {
            browser?.let {
                it.zoomLevel = it.zoomLevel - 0.25
            }
            return true
        }
        if (event.modifiers == EventFlags.EVENTFLAG_ALT_DOWN && event.windows_key_code == KeyEvent.VK_LEFT) {
            browser?.let {
                if (it.canGoBack()) {
                    it.goBack()
                }
            }
            return true
        }
        if (event.modifiers == EventFlags.EVENTFLAG_ALT_DOWN && event.windows_key_code == KeyEvent.VK_RIGHT) {
            browser?.let {
                if (it.canGoForward()) {
                    it.goForward()
                }
            }
            return true
        }
        if (event.modifiers == EventFlags.EVENTFLAG_SHIFT_DOWN or EventFlags.EVENTFLAG_CONTROL_DOWN
            && event.windows_key_code == KeyEvent.VK_O) {
            search(selectedText())
            return true
        }
        if (event.modifiers == EventFlags.EVENTFLAG_ALT_DOWN or EventFlags.EVENTFLAG_CONTROL_DOWN
            && event.windows_key_code == KeyEvent.VK_O) {
            browsePage(selectedText())
            return true
        }
        if (event.modifiers == EventFlags.EVENTFLAG_CONTROL_DOWN
            && event.windows_key_code == KeyEvent.VK_K) {
            (viewModel.currentTab() as? WebTab)?.id()?.let { id ->
                object : KoinComponent { val viewModel: WebTabViewModel by inject() }.viewModel.switchDevTools(id)
            }
            return true
        }
        if (event.modifiers == EventFlags.EVENTFLAG_SHIFT_DOWN
            && event.windows_key_code == KeyEvent.VK_P) {
            val folder = Path.of("user/screenshot")
            if (Files.exists(folder).not()) {
                Files.createDirectories(folder)
            }
            val outputStream = Files.newOutputStream(folder.resolve("${UUID.randomUUID()}.png"))
            outputStream.use {
                browser ?: return true
                val p = Point(0, 0)
                SwingUtilities.convertPointToScreen(p, browser.uiComponent)
                val region = browser.uiComponent.bounds
                region.x = p.x
                region.y = p.y

                val screenshot = Robot().createScreenCapture(region)
                ImageIO.write(screenshot, "png", it)
            }
            return true
        }

        if (event.windows_key_code == KeyEvent.VK_F12) {
            (viewModel.currentTab() as? WebTab)?.id()?.let { id ->
                webTabViewModel.switchDevTools(id)
            }
            return true
        }

        return false
    }

}