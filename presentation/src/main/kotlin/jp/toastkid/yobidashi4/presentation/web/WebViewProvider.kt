package jp.toastkid.yobidashi4.presentation.web

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import java.awt.Color
import javax.swing.JDialog
import javax.swing.WindowConstants
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WebViewProvider : KoinComponent {

    private val webViewPool: WebViewPool by inject()

    private val mainViewModel: MainViewModel by  inject()

    internal var size = IntSize.Zero

    @Composable
    fun view(id: String, initialUrl: String) {
        val background = Color(MaterialTheme.colors.surface.toArgb())
        val focusRequester = remember { FocusRequester() }

        Column {
            SwingPanel(
                factory = {
                    val component = webViewPool.component(id, initialUrl)
                    component.background = background
                    component.setSize(1281, 1040)
                    component.requestFocus()
                    component
                },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .focusRequester(focusRequester)
            )

            Spacer(modifier = Modifier.height(if (mainViewModel.showingSnackbar()) 48.dp else 0.dp))
        }

        LaunchedEffect(id) {
            val viewModel = object : KoinComponent { val vm: MainViewModel by inject() }.vm
            withContext(Dispatchers.IO) {
                viewModel.finderFlow().collect {
                    webViewPool.find(id, it.target, it.upper.not())
                }
            }
        }

        SideEffect {
            focusRequester.requestFocus()
        }
    }

    fun find(id: String, text: String) {
        webViewPool.find(id, text, true)
    }

    fun findUp(id: String, text: String) {
        webViewPool.find(id, text, false)
    }

    fun reload(id: String) {
        webViewPool.reload(id)
    }

    fun switchDevTools(id: String) {
        //showDevTool.value = showDevTool.value.not()
        val devToolsDialog = JDialog()
        devToolsDialog.defaultCloseOperation = WindowConstants.HIDE_ON_CLOSE
        devToolsDialog.setSize(800, 600)
        devToolsDialog.add(webViewPool.devTools(id))
        devToolsDialog.isVisible = true
    }

}