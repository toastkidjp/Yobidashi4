package jp.toastkid.yobidashi4.presentation.log.viewer

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.tab.TextFileViewerTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun TextFileViewerTabView(tab: TextFileViewerTab) {
    val scrollState = rememberScrollState()
    val textState = remember { mutableStateOf("") }

    LaunchedEffect(tab.path()) {
        if (Files.exists(tab.path()).not()) {
            return@LaunchedEffect
        }

        withContext(Dispatchers.IO) {
            textState.value = Files.readAllLines(tab.path()).joinToString("\n")
        }
    }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Box() {
            Text(
                textState.value,
                modifier = Modifier.padding(8.dp).verticalScroll(scrollState)
            )
            VerticalScrollbar(adapter = rememberScrollbarAdapter(scrollState), modifier = Modifier.fillMaxHeight().align(
                Alignment.CenterEnd))
        }
    }
}