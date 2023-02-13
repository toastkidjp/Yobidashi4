package jp.toastkid.yobidashi4.presentation.web

//EXPERIMENTAL FOCUS API
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.browser.BrowserPool
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BrowserView : KoinComponent {

    private val browserPool: BrowserPool by inject()

    private var location = IntOffset.Zero

    internal var size = IntSize.Zero

    private val showDevTool = mutableStateOf(false)

    @Composable
    fun view(id: String, initialUrl: String) {
        val component = browserPool.component(id, initialUrl)
        component.isVisible = true
        val focusRequester = remember { FocusRequester() }

        Box (
            modifier = Modifier.background(color = Color.White)
                .fillMaxSize()
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    size = IntSize(placeable.width, placeable.height)
                    updateBounds()
                    layout(placeable.width, placeable.height) {
                        placeable.placeRelative(0, 0)
                    }
                }
                .onGloballyPositioned { coordinates ->
                    location = IntOffset(
                        coordinates.positionInWindow().x.toInt(),
                        coordinates.positionInWindow().y.toInt()
                    )
                }
                .focusRequester(focusRequester)
                .clickable { focusRequester.requestFocus() }
        ) {
            SwingPanel(
                factory = {
                    component
                }
            )

            if (showDevTool.value) {
                SwingPanel(
                    factory = {
                        browserPool.devTools(id)
                    },
                    modifier = Modifier.height(300.dp)
                )
            }
        }

        LaunchedEffect(id) {
            val viewModel = object : KoinComponent { val vm: MainViewModel by inject() }.vm
            withContext(Dispatchers.IO) {
                viewModel.finderFlow().collect {
                    browserPool.find(id, it.target, it.upper.not())
                }
            }
        }

        DisposableEffect(component) {
            onDispose {
                component.isVisible = false
            }
        }
    }

    private fun updateBounds() {
        browserPool?.onLayout(location.x, location.y, size.width, size.height)
    }

    fun find(id: String, text: String) {
        browserPool?.find(id, text, true)
    }

    fun findUp(id: String, text: String) {
        browserPool?.find(id, text, false)
    }

    fun reload(id: String) {
        browserPool.reload(id)
    }

    fun switchDevTools() {
        showDevTool.value = showDevTool.value.not()
    }

}