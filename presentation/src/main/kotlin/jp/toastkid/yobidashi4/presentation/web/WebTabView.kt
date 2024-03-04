package jp.toastkid.yobidashi4.presentation.web

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import java.awt.Color
import java.awt.Container
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun WebTabView(tab: WebTab) {
    if (tab.isReadableUrl().not()) {
        return
    }

    val background = Color(MaterialTheme.colors.surface.toArgb())

    val viewModel = remember { WebTabViewModel() }

    val component = remember { Container() }

    LaunchedEffect(tab.id()) {
        component.background = background
        component.removeAll()
        val component1 = viewModel.component(tab)
        //component1.size = Dimension(component.parent.width, component.parent.height)
        component.add(component1)

        withContext(Dispatchers.IO) {
            viewModel.start(tab.id())
        }
    }

    Column {
        SwingPanel(
            factory = {
                component
            },
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .focusRequester(viewModel.focusRequester())
                .onSizeChanged {
                    if (component.components.isEmpty()) {
                        return@onSizeChanged
                    }

                    component.components[0].setSize(it.width - 180.dp.value.toInt(), it.height)
                }
        )

        Spacer(modifier = Modifier.height(viewModel.spacerHeight()))
    }
}